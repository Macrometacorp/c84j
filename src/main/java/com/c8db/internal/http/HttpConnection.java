/*
 * Copyright (c) 2021 - 2023 Macrometa Corp All rights reserved
 */

package com.c8db.internal.http;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8DBException;
import com.c8db.Protocol;
import com.c8db.Service;
import com.c8db.internal.C8RequestParam;
import com.c8db.internal.net.Connection;
import com.c8db.internal.net.HostDescription;
import com.c8db.internal.util.CURLLogger;
import com.c8db.internal.util.IOUtils;
import com.c8db.internal.util.ResponseUtils;
import com.c8db.util.BackoffRetryCounter;
import com.c8db.util.RequestBackoffRetryCounter;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer.Options;
import com.c8db.velocystream.BinaryRequestBody;
import com.c8db.velocystream.JsonRequestBody;
import com.c8db.velocystream.MultipartResponseBody;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestBody;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLContext;

import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;

public class HttpConnection implements Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnection.class);
    private static final ContentType CONTENT_TYPE_APPLICATION_JSON_UTF8 = ContentType.create("application/json",
            "utf-8");
    private static final ContentType CONTENT_TYPE_VPACK = ContentType.create("application/x-velocypack");
    // It is temporary solution until jwt-rotation comes.
    private static final String SERVICE_USER_EMAIL = "service@macrometa.io";
    private final PoolingHttpClientConnectionManager cm;
    private final CloseableHttpClient client;
    private final Integer responseSizeLimit;
    private final String user;
    private final String password;
    private final String email;
    private final Boolean jwtAuthEnabled;
    private final C8Serialization util;
    private final Boolean useSsl;
    private final Protocol contentType;
    private final HostDescription host;
    private volatile String jwt;
    private volatile String defaultJWT;
    private final String apiKey;
    private final HostDescription auxHost;
    private final Service service;
    private final Integer retryTimeout;

    private HttpConnection(final HostDescription host, final Integer timeout, final Integer responseSizeLimit,
                           final String user, final String password,
                           final String email, final Boolean jwtAuthEnabled, final Boolean useSsl,
                           final SSLContext sslContext, final C8Serialization util,
                           final Protocol contentType, final Long ttl, final String httpCookieSpec,
                           final String jwt, final String apiKey, final HostDescription auxHost,
                           final Service service, final Integer retryTimeout) {

        super();
        this.host = host;
        this.responseSizeLimit = responseSizeLimit;
        this.user = user;
        this.password = password;
        this.email = email;
        this.jwtAuthEnabled = jwtAuthEnabled;
        this.useSsl = useSsl;
        this.util = util;
        this.contentType = contentType;
        this.defaultJWT = jwt;
        this.apiKey = apiKey;
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
    }

    private static String buildUrl(final String baseUrl, final Request request, final Service service) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder().append(baseUrl);
        final String database = request.getDatabase();
        final String tenant = request.getTenant();

        if (tenant != null && !tenant.isEmpty() && service != Service.C8FUNCTION) {
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
        final String url = buildUrl(buildBaseUrl(host), request, service);
        final HttpRequestBase httpRequest = buildHttpRequestBase(request, url);
        httpRequest.setHeader("User-Agent", "Mozilla/5.0 (compatible; C8DB-JavaDriver/1.1; +http://mt.orz.at/)");

        if (contentType == Protocol.HTTP_VPACK) {
            httpRequest.setHeader("Accept", "application/x-velocypack");
        }
        httpRequest.setHeader("x-gdn-tenantid", request.getTenant());
        // TODO: it is temporal solution for FaaS server.
        // "internalRequest" will be changed to encoded value to make sure that this call was made inside of cluster
        if (StringUtils.isNotEmpty(System.getenv("C8_SVCUSER"))) {
            httpRequest.setHeader("x-c8-requester", "internalRequest");
        }
        addHeader(request, httpRequest);
        if (jwtAuthEnabled) {
            updateJWT();
            if (StringUtils.isNotEmpty(apiKey) && jwt == null) {  //Use API key onlu if API Key is provided
                LOGGER.debug("Using API Key for authenication.");
                httpRequest.addHeader("Authorization", "apikey " + apiKey);
            } else if (jwt == null) { //Generate JWT using user credentials if jwt and apikey are absent
                addJWT(request);
                LOGGER.debug("Using JWT for authentication.");
                httpRequest.addHeader("Authorization", "bearer " + jwt);
            } else { //Add Header when JWT is provided
                LOGGER.debug("Using JWT for authentication.");
                httpRequest.addHeader("Authorization", "bearer " + jwt);
            }
        } else {
            // basic auth instead
            LOGGER.debug("Using Credentials for authenication.");
            final Credentials credentials = addCredentials(httpRequest);
            if (LOGGER.isDebugEnabled()) {
                CURLLogger.log(url, request, credentials, util);
            }
        }
        Response response = null;
        try {
            response = buildResponse(client.execute(httpRequest));
            checkError(response);
        } catch (C8DBException ex) {
            if (ex.getResponseCode().equals(401)) {
                // jwt might has expired refresh it
                addJWT(request);
                httpRequest.removeHeaders("Authorization");
                httpRequest.addHeader("Authorization", "bearer " + jwt);
                response = buildResponse(client.execute(httpRequest));
                checkError(response);
            } else if (ex.getResponseCode() >= 500) {
                if (request.isRetryEnabled()) {
                    response = retryRequest(request, httpRequest);
                }
                checkError(response);
            } else if (ex.getResponseCode() >= 400) {
                // Handle HTTP Error messages.
                checkError(response);
            } else {
                checkError(response);
            }
        } catch (UnknownHostException | NoHttpResponseException | ConnectException ex) {
            response = retryRequest(request, httpRequest);
            if(response == null){
                throw new C8DBException("c84j exhausted all retries.", SC_SERVICE_UNAVAILABLE, ex);
            }
        }
        return response;
    }

    private Response retryRequest(final Request request, HttpRequestBase httpRequest) throws IOException {
        Response response = null;

        BackoffRetryCounter retryCounter = new RequestBackoffRetryCounter(request, retryTimeout);
        while (retryCounter.canRetry()) {
            try {
                LOGGER.info(String.format("Retrying request to %s in %s...", service.name(),
                        retryCounter.getTimeInterval()));
                Thread.sleep(retryCounter.getTimeIntervalMillis());
                response = buildResponse(client.execute(httpRequest));
                checkError(response);

                return response;
            } catch (InterruptedException e) {
            } catch (Exception e) {
                if (e instanceof C8DBException && ((C8DBException) e).getResponseCode().equals(401)) {
                    // jwt might has expired refresh it
                    addJWT(request);
                    httpRequest.removeHeaders("Authorization");
                    httpRequest.addHeader("Authorization", "bearer " + jwt);
                }
            }
            retryCounter.increment();
        }

        LOGGER.info(String.format("Unable to connect to the C8DB after %s. No more retries will be made",
                retryCounter.getTimeInterval()));
        return response;
    }

    private void updateJWT() {
        if (StringUtils.isNotEmpty(user) && !host.getHost().equals(auxHost.getHost())) {
            jwt = null;
        } else {
            jwt = defaultJWT;
        }
    }

    private synchronized void addJWT(final Request request) throws IOException {
        addServiceJWT();
        if(StringUtils.isNotEmpty(user)
                && !host.getHost().equals(auxHost.getHost())
                && service != Service.C8FUNCTION
                && service != Service.C8CEP
                && service != Service.C8KMS) {
            addUserJWT(request.getTenant(), user);
        }
    }

    private synchronized void addServiceJWT() throws IOException {
        String suffix = SERVICE_USER_EMAIL.equals(email) ? "/_open/auth/internal" : "/_open/auth";
        String authUrl = buildBaseUrl(auxHost) + suffix;
        Map<String, String> credentials = new HashMap<String, String>();

        credentials.put("username", user);
        credentials.put("password", password);
        credentials.put("email", email);
        final HttpRequestBase authHttpRequest = buildHttpRequestBase(
            new Request("_mm", C8RequestParam.SYSTEM, RequestType.POST, authUrl)
                .setBody(util.serialize(credentials)),
            authUrl);
        authHttpRequest.setHeader("User-Agent", "Mozilla/5.0 (compatible; C8DB-JavaDriver/1.1; +http://mt.orz.at/)");
        if (contentType == Protocol.HTTP_VPACK) {
            authHttpRequest.setHeader("Accept", "application/x-velocypack");
        }
        Response authResponse = buildResponse(client.execute(authHttpRequest));
        checkError(authResponse);
        defaultJWT = authResponse.getBody().get("jwt").getAsString();
        setJwt(defaultJWT);
    }

    private synchronized void addUserJWT(String tenant, String user) throws IOException {
        String authUrl = new StringBuilder(buildBaseUrl(host))
                .append("/_tenant/").append(tenant)
                .append("/_fabric/").append(C8RequestParam.SYSTEM)
                .append("/_api/streams/user/").append(user)
                .append("/jwt").toString();

        final HttpRequestBase authHttpRequest = buildHttpRequestBase(
            new Request(tenant, C8RequestParam.SYSTEM, RequestType.POST, authUrl),
            authUrl);
        authHttpRequest.setHeader("User-Agent", "Mozilla/5.0 (compatible; C8DB-JavaDriver/1.1; +http://mt.orz.at/)");
        authHttpRequest.setHeader("Authorization", "bearer " + jwt);
        if (contentType == Protocol.HTTP_VPACK) {
            authHttpRequest.setHeader("Accept", "application/x-velocypack");
        }
        Response authResponse = buildResponse(client.execute(authHttpRequest));
        checkError(authResponse);
        setJwt(authResponse.getBody().get("result").getAsString());
    }

    private HttpRequestBase buildHttpRequestBase(final Request request, final String url) {
        final HttpRequestBase httpRequest;
        switch (request.getRequestType()) {
            case POST:
                httpRequest = requestWithBody(new HttpPost(url), request);
                break;
            case PUT:
                httpRequest = requestWithBody(new HttpPut(url), request);
                break;
            case PATCH:
                httpRequest = requestWithBody(new HttpPatch(url), request);
                break;
            case DELETE:
                httpRequest = requestWithBody(new HttpDeleteWithBody(url), request);
                break;
            case HEAD:
                httpRequest = new HttpHead(url);
                break;
            case GET:
            default:
                httpRequest = new HttpGet(url);
                break;
        }
        return httpRequest;
    }

    private HttpRequestBase requestWithBody(final HttpEntityEnclosingRequestBase httpRequest, final Request request) {
        final RequestBody body = request.getBody();
        if (body != null) {
            if (contentType == Protocol.HTTP_VPACK) {
                if (body instanceof JsonRequestBody) {
                    VPackSlice vPackSlice = ((JsonRequestBody) body).getValue();
                    httpRequest.setEntity(new ByteArrayEntity(
                            Arrays.copyOfRange(vPackSlice.getBuffer(), vPackSlice.getStart(),
                                    vPackSlice.getStart() + vPackSlice.getByteSize()),
                            CONTENT_TYPE_VPACK));
                } else {
                    throw new C8DBException("This protocol doesn't support this type of body " + body.getClass());
                }

            } else {
                if (body instanceof JsonRequestBody) {
                    VPackSlice vPackSlice = ((JsonRequestBody) body).getValue();
                    httpRequest.setEntity(new StringEntity(vPackSlice.toString(), CONTENT_TYPE_APPLICATION_JSON_UTF8));
                } else if (body instanceof BinaryRequestBody) {
                    BinaryRequestBody binaryBody = (BinaryRequestBody) body;
                    final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    for (BinaryRequestBody.Item item : binaryBody.getItems()) {
                        builder.addTextBody("meta", item.getMeta().toString(), ContentType.APPLICATION_JSON);
                        builder.addBinaryBody("value", item.getValue(), ContentType.APPLICATION_OCTET_STREAM, "");
                    }
                    final HttpEntity entity = builder.build();
                    httpRequest.setEntity(entity);
                } else {
                    throw new C8DBException("This protocol doesn't support this type of body " + body.getClass() );
                }

            }
        }
        return httpRequest;
    }

    private String buildBaseUrl(final HostDescription host) {
        return (Boolean.TRUE == useSsl ? "https://" : "http://") + host.getHost() + ":" + host.getPort()
                + (host.getPath() != null ? host.getPath() : "");
    }

    public Credentials addCredentials(final HttpRequestBase httpRequest) {
        Credentials credentials = null;
        if (user != null) {
            credentials = new UsernamePasswordCredentials(user, password != null ? password : "");
            try {
                httpRequest.addHeader(new BasicScheme().authenticate(credentials, httpRequest, null));
            } catch (final AuthenticationException e) {
                throw new C8DBException(e);
            }
        }
        return credentials;
    }

    public Response buildResponse(final CloseableHttpResponse httpResponse)
            throws UnsupportedOperationException, IOException {
        final Response response = new Response();
        response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        final HttpEntity entity = httpResponse.getEntity();
        if (entity != null && entity.getContent() != null) {
            if (contentType == Protocol.HTTP_VPACK) {
                final byte[] content = IOUtils.toByteArray(entity.getContent());
                if (content.length > 0) {
                    response.setBody(new VPackSlice(content));
                }
            } else {
                Header[] httpContentTypes = httpResponse.getHeaders("Content-Type");
                String httpContentType = httpContentTypes.length > 0 ?
                        httpContentTypes[0].getValue() : "application/json; charset=utf-8";
                if (httpContentType.startsWith("multipart/form-data")) {
                    try {
                        ByteArrayDataSource datasource = new ByteArrayDataSource(entity.getContent(), httpContentType);
                        System.setProperty("mail.mime.multipart.allowempty", "true");
                        MimeMultipart multipart = new MimeMultipart(datasource);
                        List<MultipartResponseBody.Item> items = new ArrayList<>();
                        int count = multipart.getCount();
                        for (int i = 0; i < count; i++) {
                            Object value = null;
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            if (bodyPart.isMimeType(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
                                value = IOUtils.toByteArray(bodyPart.getInputStream());
                            } else if (bodyPart.isMimeType(ContentType.APPLICATION_JSON.getMimeType())) {
                                final String content = IOUtils.toString(bodyPart.getInputStream());
                                if (!content.isEmpty()) {
                                    try {
                                        value = util.serialize(content,
                                                new Options().stringAsJson(true).serializeNullValues(true));
                                    } catch (C8DBException e) {
                                        final byte[] contentAsByteArray = content.getBytes();
                                        if (contentAsByteArray.length > 0) {
                                            response.setBody(new VPackSlice(contentAsByteArray));
                                        }
                                    }
                                }
                            }
                            items.add(new MultipartResponseBody.Item(value, bodyPart.getContentType(),
                                    bodyPart.getFileName()));
                        }
                        response.setBody(new MultipartResponseBody(items));
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                } else {
                    final String content = IOUtils.toString(entity.getContent());
                    if (!content.isEmpty()) {
                        try {
                            response.setBody(
                                    util.serialize(content, new Options().stringAsJson(true).serializeNullValues(true)));
                        } catch (C8DBException e) {
                            final byte[] contentAsByteArray = content.getBytes();
                            if (contentAsByteArray.length > 0) {
                                response.setBody(new VPackSlice(contentAsByteArray));
                            }
                        }
                    }
                }
            }
        }
        final Header[] headers = httpResponse.getAllHeaders();
        final Map<String, String> meta = response.getMeta();
        for (final Header header : headers) {
            meta.put(header.getName(), header.getValue());
        }
        return response;
    }

    protected void checkError(final Response response) throws C8DBException {
        ResponseUtils.checkError(util, response);
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public static class Builder {

        private String user;
        private String password;
        private String email;
        private Boolean jwtAuthEnabled;
        private C8Serialization util;
        private Boolean useSsl;
        private String httpCookieSpec;
        private Protocol contentType;
        private HostDescription host;
        private Long ttl;
        private SSLContext sslContext;
        private Integer responseSizeLimit;
        private Integer timeout;
        private String jwt;
        private String apiKey;
        private HostDescription auxHost;
        private Service service;
        private Integer retryTimeout;

        public Builder user(final String user) {
            this.user = user;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder jwt(final String jwt) {
            this.jwt = jwt;
            return this;
        }

        public Builder auxHost(final HostDescription auxHost) {
            this.auxHost = auxHost;
            return this;
        }

        public Builder apiKey(final String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder serializationUtil(final C8Serialization util) {
            this.util = util;
            return this;
        }

        public Builder jwtAuthEnabled(final Boolean jwtAuth) {
            this.jwtAuthEnabled = jwtAuth;
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
            return new HttpConnection(host, timeout, responseSizeLimit, user, password, email, jwtAuthEnabled, useSsl, sslContext, util,
                    contentType, ttl, httpCookieSpec, jwt, apiKey, auxHost, service, retryTimeout);
        }
    }

}
