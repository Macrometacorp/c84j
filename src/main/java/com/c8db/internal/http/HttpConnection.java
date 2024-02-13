/*
 * Copyright (c) 2021 - 2024 Macrometa Corp All rights reserved
 */

package com.c8db.internal.http;

import com.c8db.C8DBException;
import com.c8db.Protocol;
import com.c8db.SecretProvider;
import com.c8db.Service;
import com.c8db.credentials.ApiKeyCredentials;
import com.c8db.credentials.BasicCredentials;
import com.c8db.credentials.C8Credentials;
import com.c8db.credentials.DefaultCredentials;
import com.c8db.credentials.JwtCredentials;
import com.c8db.internal.C8RemoteSecretProvider;
import com.c8db.internal.SecretProviderContext;
import com.c8db.internal.net.Connection;
import com.c8db.internal.net.HostDescription;
import com.c8db.internal.util.CURLLogger;
import com.c8db.internal.util.RequestUtils;
import com.c8db.internal.util.ResponseUtils;
import com.c8db.util.BackoffRetryCounter;
import com.c8db.util.C8Serialization;
import com.c8db.util.RequestBackoffRetryCounter;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;

import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;

public class HttpConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnection.class);

    private final PoolingHttpClientConnectionManager cm;
    private final CloseableHttpClient client;
    private final C8Credentials credentials;
    private final C8Serialization util;
    private final Boolean useSsl;
    private final Protocol contentType;
    private final HostDescription host;
    private Map<TenantUser, String> cachedJwt = new ConcurrentHashMap<>();
    private final HostDescription auxHost;
    private final SecretProvider secretProvider;
    private final Service service;
    private final Integer retryTimeout;

    private HttpConnection(final C8Credentials credentials, final HostDescription host, final Integer timeout,
                           final Integer responseSizeLimit, final Boolean useSsl, final SSLContext sslContext,
                           final C8Serialization util, final Protocol contentType, final Long ttl,
                           final String httpCookieSpec, final HostDescription auxHost,
                           final SecretProvider secretProvider, final Service service, Integer retryTimeout) {

        super();
        this.credentials = credentials;
        this.host = host;
        this.useSsl = useSsl;
        this.util = util;
        this.contentType = contentType;
        this.auxHost = auxHost;
        this.service = service;
        this.retryTimeout = retryTimeout;

        final RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
                .create();
        if (Boolean.TRUE == useSsl) {
            if (sslContext != null) {
                registryBuilder.register("https", new SSLConnectionSocketFactory(sslContext));
            } else {
                registryBuilder.register("https", new SSLConnectionSocketFactory(SSLContexts.createSystemDefault()));
            }
        } else {
            registryBuilder.register("http", new PlainConnectionSocketFactory());
        }
        MessageConstraints messageConstraints = MessageConstraints.custom()
            .setMaxLineLength(responseSizeLimit)
            .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Consts.UTF_8)
            .setMessageConstraints(messageConstraints)
            .build();

        cm = new PoolingHttpClientConnectionManager(registryBuilder.build());
        cm.setDefaultConnectionConfig(connectionConfig);
        cm.setDefaultMaxPerRoute(1);
        cm.setMaxTotal(1);
        final RequestConfig.Builder requestConfig = RequestConfig.custom();
        if (timeout != null && timeout >= 0) {
            requestConfig.setConnectTimeout(timeout);
            requestConfig.setConnectionRequestTimeout(timeout);
            requestConfig.setSocketTimeout(timeout);
        }

        if (httpCookieSpec != null && httpCookieSpec.length() > 1) {
            requestConfig.setCookieSpec(httpCookieSpec);
        }

        final ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                return HttpConnection.this.getKeepAliveDuration(response);
            }
        };
        final HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig.build())
                .setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrategy)
                .setRetryHandler(new HttpRequestRetryHandler());
        if (ttl != null) {
            builder.setConnectionTimeToLive(ttl, TimeUnit.MILLISECONDS);
        }
        client = builder.build();

        SecretProviderContext secCtx =
                SecretProviderContext.builder().credentials(credentials).useSsl(useSsl).client(client).host(auxHost).
                        serialization(util).contentType(contentType).build();
        this.secretProvider =
                secretProvider == null ? new C8RemoteSecretProvider() : secretProvider;
        this.secretProvider.init(secCtx);
    }

    private static String buildUrl(final String baseUrl, final Request request, final Service service) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder().append(baseUrl);
        final String database = request.getPathDatabase();
        final String tenant = request.getPathTenant();
        if (StringUtils.isNotEmpty(tenant)) {
            sb.append("/_tenant/").append(tenant);
        }

        if (database != null && !database.isEmpty()) {
            sb.append("/_fabric/").append(database);
        }

        sb.append(request.getRequest());
        if (!request.getQueryParam().isEmpty()) {
            if (request.getRequest().contains("?")) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            final String paramString = URLEncodedUtils.format(toList(request.getQueryParam()), "utf-8");
            sb.append(paramString);
        }
        return sb.toString();
    }

    private static List<NameValuePair> toList(final Map<String, String> parameters) {
        final ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>(parameters.size());
        for (final Entry<String, String> param : parameters.entrySet()) {
            if (param.getValue() != null) {
                paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }
        return paramList;
    }

    private static void addHeader(final Request request, final HttpRequestBase httpRequest) {
        for (final Entry<String, String> header : request.getHeaderParam().entrySet()) {
            httpRequest.addHeader(header.getKey(), header.getValue());
        }
    }

    private long getKeepAliveDuration(final HttpResponse response) {
        final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            final HeaderElement he = it.nextElement();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && "timeout".equalsIgnoreCase(param)) {
                try {
                    return Long.parseLong(value) * 1000L;
                } catch (final NumberFormatException ignore) {
                }
            }
        }
        return 30L * 1000L;
    }

    @Override
    public void close() throws IOException {
        cm.shutdown();
        client.close();
    }

    public Response execute(final Request request) throws C8DBException, IOException {
        final String url = buildUrl(RequestUtils.buildBaseUrl(host, useSsl), request, service);
        final HttpRequestBase httpRequest = RequestUtils.buildHttpRequestBase(request, url, contentType);
        httpRequest.setHeader(HttpHeaders.USER_AGENT,
            "Mozilla/5.0 (compatible; C8DB-JavaDriver/1.1; +http://mt.orz.at/)");

        if (contentType == Protocol.HTTP_VPACK) {
            httpRequest.setHeader(HttpHeaders.ACCEPT, "application/x-velocypack");
        }
        addHeader(request, httpRequest);

        C8Credentials credentials = request.getCredentials() == null ? this.credentials : request.getCredentials();
        if (credentials instanceof BasicCredentials) {
            BasicCredentials basicCredentials = (BasicCredentials) credentials;
            LOGGER.debug("Using Credentials for authentication.");
            final Credentials httpCredentials = addCredentials(httpRequest, basicCredentials);
            if (LOGGER.isDebugEnabled()) {
                CURLLogger.log(url, request, httpCredentials, util);
            }
        } else if (credentials instanceof JwtCredentials) {
            String jwt = ((JwtCredentials) credentials).getJwt();
            LOGGER.debug("Using JWT for authentication.");
            httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwt);
        } else if (credentials instanceof ApiKeyCredentials) {
            String apiKey = ((ApiKeyCredentials) credentials).getApiKey();
            LOGGER.debug("Using API Key for authentication.");
            httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "apikey " + apiKey);
        } else if (credentials instanceof DefaultCredentials) {
            DefaultCredentials defaultCredentials = (DefaultCredentials) credentials;
            TenantUser tenantUser = new TenantUser(request.getDbTenant(), defaultCredentials.getUser());
            String jwt = cachedJwt.get(tenantUser);
            if (StringUtils.isEmpty(jwt)) {
                    jwt = addJWT(tenantUser, request.getCredentials());
                    LOGGER.debug("Using JWT for authentication.");
                }
            httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwt);
        }
        Response response = null;
        try {
            response = ResponseUtils.buildResponse(util, client.execute(httpRequest), contentType);
            ResponseUtils.checkError(util, response);
        } catch (C8DBException ex) {
            if (ex.getResponseCode().equals(401)) {
                if (credentials instanceof DefaultCredentials) {
                    // jwt might have expired refresh it
                    DefaultCredentials defaultCredentials = (DefaultCredentials) credentials;
                    TenantUser tenantUser = new TenantUser(request.getDbTenant(), defaultCredentials.getUser());
                    String jwt = addJWT(tenantUser, request.getCredentials());
                    httpRequest.removeHeaders(HttpHeaders.AUTHORIZATION);
                    httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwt);
                    response = ResponseUtils.buildResponse(util, client.execute(httpRequest), contentType);
                    ResponseUtils.checkError(util, response);
                } else {
                    ResponseUtils.checkError(util, response);
                }
            } else if (ex.getResponseCode() >= 500) {
                if (request.isRetryEnabled()) {
                    response = retryRequest(request, httpRequest, credentials);
                }
                ResponseUtils.checkError(util, response);
            } else if (ex.getResponseCode() >= 400) {
                // Handle HTTP Error messages.
                ResponseUtils.checkError(util, response);
            } else {
                ResponseUtils.checkError(util, response);
            }
        } catch (UnknownHostException | NoHttpResponseException | ConnectException ex) {
            response = retryRequest(request, httpRequest, credentials);
            if(response == null){
                throw new C8DBException("c84j exhausted all retries.", SC_SERVICE_UNAVAILABLE, ex);
            }
        }
        return response;
    }

    private Response retryRequest(final Request request, HttpRequestBase httpRequest, C8Credentials credentials) throws IOException {
        Response response = null;

        BackoffRetryCounter retryCounter = new RequestBackoffRetryCounter(request, retryTimeout);
        while (retryCounter.canRetry()) {
            try {
                LOGGER.info(String.format("Retrying request to %s in %s...", service.name(),
                        retryCounter.getTimeInterval()));
                Thread.sleep(retryCounter.getTimeIntervalMillis());
                response = ResponseUtils.buildResponse(util, client.execute(httpRequest), contentType);
                ResponseUtils.checkError(util, response);

                return response;
            } catch (InterruptedException e) {
            } catch (Exception e) {
                if (e instanceof C8DBException && ((C8DBException) e).getResponseCode().equals(401)) {
                    if (credentials instanceof DefaultCredentials) {
                        DefaultCredentials defaultCredentials = (DefaultCredentials) credentials;
                        // jwt might have expired refresh it
                        String jwt = addJWT(new TenantUser(request.getDbTenant(), defaultCredentials.getUser()),
                                request.getCredentials());
                        httpRequest.removeHeaders(HttpHeaders.AUTHORIZATION);
                        httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwt);
                    }
                }
            }
            retryCounter.increment();
        }


        LOGGER.info(String.format("Unable to connect to the C8DB after %s. No more retries will be made",
                retryCounter.getTimeInterval()));
        return response;
    }

    private synchronized String addJWT(TenantUser tenantUser, C8Credentials requestCredentials) {
        String secret = null;
        if (requestCredentials != null) {
            // use temporary provider
            SecretProviderContext context = SecretProviderContext.builder()
                    .client(client)
                    .useSsl(useSsl)
                    .host(host)
                    .serialization(util)
                    .contentType(contentType)
                    .credentials(requestCredentials)
                    .build();
            secretProvider.init(context);
            secret = secretProvider.fetchSecret(tenantUser.tenant, tenantUser.user);
            context = SecretProviderContext.builder()
                    .client(client)
                    .useSsl(useSsl)
                    .host(host)
                    .serialization(util)
                    .contentType(contentType)
                    .credentials(credentials)
                    .build();
            secretProvider.init(context);
        } else {
            secret = secretProvider.fetchSecret(tenantUser.tenant, tenantUser.user);
        }
        cachedJwt.put(tenantUser, secret);
        return secret;
    }

    public Credentials addCredentials(final HttpRequestBase httpRequest, BasicCredentials basicCredentials) {
        Credentials credentials = null;
        if (basicCredentials.getUser() != null) {
            credentials = new UsernamePasswordCredentials(basicCredentials.getUser(),
                    new String(basicCredentials.getPassword()));
            try {
                httpRequest.addHeader(new BasicScheme().authenticate(credentials, httpRequest, null));
            } catch (final AuthenticationException e) {
                throw new C8DBException(e);
            }
        }
        return credentials;
    }

    public static class Builder {

        private C8Credentials credentials;
        private C8Serialization util;
        private Boolean useSsl;
        private String httpCookieSpec;
        private Protocol contentType;
        private HostDescription host;
        private Long ttl;
        private SSLContext sslContext;
        private Integer timeout;
        private Integer responseSizeLimit;
        private HostDescription auxHost;
        private SecretProvider secretProvider;
        private Service service;
        private Integer retryTimeout;

        public Builder credentials(final C8Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder auxHost(final HostDescription auxHost) {
            this.auxHost = auxHost;
            return this;
        }

        public Builder serializationUtil(final C8Serialization util) {
            this.util = util;
            return this;
        }

        public Builder useSsl(final Boolean useSsl) {
            this.useSsl = useSsl;
            return this;
        }

        public Builder httpCookieSpec(String httpCookieSpec) {
            this.httpCookieSpec = httpCookieSpec;
            return this;
        }

        public Builder contentType(final Protocol contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder host(final HostDescription host) {
            this.host = host;
            return this;
        }

        public Builder ttl(final Long ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder sslContext(final SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Builder timeout(final Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder secretProvider(final SecretProvider secretProvider) {
            this.secretProvider = secretProvider;
            return this;
        }

        public Builder responseSizeLimit(final Integer responseSizeLimit) {
            this.responseSizeLimit = responseSizeLimit;
            return this;
        }

        public Builder service(final Service service) {
            this.service = service;
            return this;
        }
        public Builder retryTimeout(final Integer retryTimeout) {
            this.retryTimeout = retryTimeout;
            return this;
        }


        public HttpConnection build() {
            return new HttpConnection(credentials, host, timeout, responseSizeLimit, useSsl, sslContext, util,
                    contentType, ttl, httpCookieSpec, auxHost, secretProvider, service, retryTimeout);
        }
    }

    private static class TenantUser {

        public String tenant;
        public String user;

        public TenantUser(String tenant, String user) {
            this.tenant = tenant;
            this.user = user;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TenantUser that = (TenantUser) o;
            return Objects.equals(tenant, that.tenant) && Objects.equals(user, that.user);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tenant, user);
        }
    }

}
