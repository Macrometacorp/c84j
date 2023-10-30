/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackParser;
import com.c8db.C8DB;
import com.c8db.C8DBException;
import com.c8db.Service;
import com.c8db.entity.LoadBalancingStrategy;
import com.c8db.internal.net.ConnectionFactory;
import com.c8db.internal.net.DirtyReadHostHandler;
import com.c8db.internal.net.ExtendedHostResolver;
import com.c8db.internal.net.FallbackHostHandler;
import com.c8db.internal.net.Host;
import com.c8db.internal.net.HostDescription;
import com.c8db.internal.net.HostHandler;
import com.c8db.internal.net.HostResolver;
import com.c8db.internal.net.RandomHostHandler;
import com.c8db.internal.net.RoundRobinHostHandler;
import com.c8db.internal.net.SimpleHostResolver;
import com.c8db.internal.util.HostUtils;
import com.c8db.internal.velocypack.VPackDriverModule;
import com.c8db.util.C8Deserializer;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public abstract class InternalC8DBBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InternalC8DBBuilder.class);

    private static final String PROPERTY_KEY_HOSTS = "c8db.hosts";
    private static final String PROPERTY_KEY_C8STREAMS_HOSTS = "c8streams.hosts";
    private static final String PROPERTY_KEY_HOST = "c8db.host";
    private static final String PROPERTY_KEY_PORT = "c8db.port";
    private static final String PROPERTY_KEY_TIMEOUT = "c8db.timeout";
    private static final String PROPERTY_KEY_RETRY_TIMEOUT = "c8db.retryTimeout";
    private static final String PROPERTY_KEY_RESPONSE_SIZE_LIMIT = "c8db.responseSizeLimit";
    private static final String PROPERTY_KEY_USER = "c8db.user";
    private static final String PROPERTY_KEY_PASSWORD = "c8db.password";
    private static final String PROPERTY_KEY_EMAIL = "c8db.email";
    private static final String PROPERTY_KEY_JWT_AUTH = "c8db.jwt";
    private static final String PROPERTY_KEY_JWT_USER = "c8db.jwtUser";
    private static final String PROPERTY_KEY_APIKEY = "c8db.apikey";
    private static final String PROPERTY_KEY_USE_SSL = "c8db.usessl";
    private static final String PROPERTY_KEY_COOKIE_SPEC = "c8db.httpCookieSpec";
    private static final String PROPERTY_KEY_V_STREAM_CHUNK_CONTENT_SIZE = "c8db.chunksize";
    private static final String PROPERTY_KEY_MAX_CONNECTIONS = "c8db.connections.max";
    private static final String PROPERTY_KEY_CONNECTION_TTL = "c8db.connections.ttl";
    private static final String PROPERTY_KEY_ACQUIRE_HOST_LIST = "c8db.acquireHostList";
    private static final String PROPERTY_KEY_ACQUIRE_HOST_LIST_INTERVAL = "c8db.acquireHostList.interval";
    private static final String PROPERTY_KEY_LOAD_BALANCING_STRATEGY = "c8db.loadBalancingStrategy";
    private static final String DEFAULT_PROPERTY_FILE = "/c8db.properties";

    protected final Map<Service, List<HostDescription>> hosts;
    protected HostDescription host;
    protected Integer responseSizeLimit;
    protected Integer timeout;
    protected String user;
    protected String password;
    protected String email;
    protected String jwtToken;
    protected Boolean jwtAuth;
    protected Boolean useSsl;
    protected String httpCookieSpec;
    protected SSLContext sslContext;
    protected Integer chunksize;
    protected Integer maxConnections;
    protected Long connectionTtl;
    protected final VPack.Builder vpackBuilder;
    protected final VPackParser.Builder vpackParserBuilder;
    protected C8Serializer serializer;
    protected C8Deserializer deserializer;
    protected Boolean acquireHostList;
    protected Integer acquireHostListInterval;
    protected LoadBalancingStrategy loadBalancingStrategy;
    protected Integer retryTimeout;
    protected C8Serialization customSerializer;
    protected String apiKey;

    public InternalC8DBBuilder() {
        super();
        vpackBuilder = new VPack.Builder();
        vpackParserBuilder = new VPackParser.Builder();
        vpackBuilder.registerModule(new VPackDriverModule());
        vpackParserBuilder.registerModule(new VPackDriverModule());
        host = new HostDescription(C8Defaults.DEFAULT_HOST, C8Defaults.DEFAULT_PORT);
        hosts = new HashMap();
        for (Service key : Service.values()) {
            hosts.put(key, new ArrayList<>());
        }
        user = C8Defaults.DEFAULT_USER;
        loadProperties(C8DB.class.getResourceAsStream(DEFAULT_PROPERTY_FILE));
    }

    public InternalC8DBBuilder loadProperties(final InputStream in) throws C8DBException {

        final Properties properties = new Properties();

        if (in != null) {

            try {
                properties.load(in);
            } catch (final IOException e) {
                throw new C8DBException(e);
            }
        }

        loadProperties(properties);

        return this;

    }

    protected void loadProperties(final Properties properties) {
        loadHosts(PROPERTY_KEY_HOSTS, Service.C8DB, properties, this.hosts);
        //loadHosts(PROPERTY_KEY_C8STREAMS_HOSTS, Service.C8STREAMS, properties, this.hosts);
        final String host = loadHost(properties, this.host.getHost());
        final int port = loadPort(properties, this.host.getPort());
        this.host = new HostDescription(host, port);
        responseSizeLimit = loadMaxResponseSize(properties, responseSizeLimit);
        timeout = loadTimeout(properties, timeout);
        user = loadUser(properties, user);
        password = loadPassword(properties, password);
        email = loadEmail(properties, email);
        jwtToken = loadJWTToken(properties, jwtToken);
        jwtAuth = loadJWTAuth(properties, jwtAuth);
        apiKey = loadApiKey(properties, apiKey);
        useSsl = loadUseSsl(properties, useSsl);
        httpCookieSpec = loadhttpCookieSpec(properties, httpCookieSpec);
        chunksize = loadChunkSize(properties, chunksize);
        maxConnections = loadMaxConnections(properties, maxConnections);
        connectionTtl = loadConnectionTtl(properties, connectionTtl);
        acquireHostList = loadAcquireHostList(properties, acquireHostList);
        acquireHostListInterval = loadAcquireHostListInterval(properties, acquireHostListInterval);
        loadBalancingStrategy = loadLoadBalancingStrategy(properties, loadBalancingStrategy);
        retryTimeout = loadRetryTimeout(properties, retryTimeout);
    }

    protected void setHost(final Service service, final String host, final int port) {
        hosts.get(service).add(new HostDescription(host, port));
    }

    protected void setHost(final Service service, final String host, final int port, final String path) {
        hosts.get(service).add(new HostDescription(host, port, path));
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected void setTimeout(final Integer timeout) {
        this.timeout = timeout;
    }

    protected void setResponseSizeLimit(final Integer responseSizeLimit) {
        this.responseSizeLimit = responseSizeLimit;
    }

    protected void setUser(final String user) {
        this.user = user;
    }

    protected void setPassword(final String password) {
        this.password = password;
    }

    protected void setUseSsl(final Boolean useSsl) {
        this.useSsl = useSsl;
    }

    protected void setSslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    protected void setChunksize(final Integer chunksize) {
        this.chunksize = chunksize;
    }

    protected void setMaxConnections(final Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    protected void setConnectionTtl(final Long connectionTtl) {
        this.connectionTtl = connectionTtl;
    }

    protected void setAcquireHostList(final Boolean acquireHostList) {
        this.acquireHostList = acquireHostList;
    }

    protected void setAcquireHostListInterval(final Integer acquireHostListInterval) {
        this.acquireHostListInterval = acquireHostListInterval;
    }

    protected void setLoadBalancingStrategy(final LoadBalancingStrategy loadBalancingStrategy) {
        this.loadBalancingStrategy = loadBalancingStrategy;
    }

    protected void serializer(final C8Serializer serializer) {
        this.serializer = serializer;
    }

    protected Map<Service, HostHandler> createHostHandlerMatrix(final HostResolver hostResolver) {
        Map<Service, HostHandler> matrix = new HashMap<Service, HostHandler>();
        for (Service service : Service.values()) {
            matrix.put(service, createHostHandler(hostResolver, service));
        }
        return Collections.unmodifiableMap(matrix);
    }

    protected HostHandler createHostHandler(final HostResolver hostResolver, final Service service) {

        final HostHandler hostHandler;

        if (loadBalancingStrategy != null) {
            switch (loadBalancingStrategy) {
                case ONE_RANDOM:
                    hostHandler = new RandomHostHandler(hostResolver, new FallbackHostHandler(hostResolver, service), service);
                    break;
                case ROUND_ROBIN:
                    hostHandler = new RoundRobinHostHandler(hostResolver, service);
                    break;
                case NONE:
                default:
                    hostHandler = new FallbackHostHandler(hostResolver, service);
                    break;
            }
        } else {
            hostHandler = new FallbackHostHandler(hostResolver, service);
        }

        LOG.debug("HostHandler is " + hostHandler.getClass().getSimpleName());

        return new DirtyReadHostHandler(hostHandler, new RoundRobinHostHandler(hostResolver, service));
    }
    protected void setRetryTimeout(final Integer retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    protected void deserializer(final C8Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    protected void setSerializer(final C8Serialization serializer) {
        this.customSerializer = serializer;
    }

    protected HostResolver createHostResolver(final Map<Service, Collection<Host>> hostsMatrix, final int maxConnections,
                                              final ConnectionFactory connectionFactory) {

        if (acquireHostList != null && acquireHostList) {
            LOG.debug("acquireHostList -> Use ExtendedHostResolver");
            return new ExtendedHostResolver(HostUtils.cloneHostMatrix(hostsMatrix), maxConnections, connectionFactory,
                    acquireHostListInterval);
        } else {
            LOG.debug("Use SimpleHostResolver");
            return new SimpleHostResolver(HostUtils.cloneHostMatrix(hostsMatrix));
        }

    }

    private static void loadHosts(final String propertyName, Service service, final Properties properties, final Map<Service, List<HostDescription>> hosts) {
        final String hostsProp = properties.getProperty(propertyName);
        if (hostsProp != null) {
            final String[] hostsSplit = hostsProp.split(",");
            for (final String host : hostsSplit) {
                final String[] split = host.split(":");
                if (split.length != 2 || !split[1].matches("[0-9]+")) {
                    throw new C8DBException(String.format(
                            "Could not load property-value c8db.hosts=%s. Expected format ip:port,ip:port,...",
                            hostsProp));
                } else {
                    hosts.get(service).add(new HostDescription(split[0], Integer.valueOf(split[1])));
                }
            }
        }
    }

    private static String loadHost(final Properties properties, final String currentValue) {
        final String host = getProperty(properties, PROPERTY_KEY_HOST, currentValue, C8Defaults.DEFAULT_HOST);
        if (host.contains(":")) {
            throw new C8DBException(String.format(
                    "Could not load property-value c8db.host=%s. Expect only ip. Do you mean c8db.hosts=ip:port ?",
                    host));
        }
        return host;
    }

    private static Integer loadPort(final Properties properties, final int currentValue) {
        return Integer.parseInt(getProperty(properties, PROPERTY_KEY_PORT, currentValue, C8Defaults.DEFAULT_PORT));
    }

    private static Integer loadTimeout(final Properties properties, final Integer currentValue) {
        return Integer
                .parseInt(getProperty(properties, PROPERTY_KEY_TIMEOUT, currentValue, C8Defaults.DEFAULT_TIMEOUT));
    }

    private static Integer loadMaxResponseSize(final Properties properties, final Integer currentValue) {
        return Integer
                .parseInt(getProperty(properties, PROPERTY_KEY_RESPONSE_SIZE_LIMIT, currentValue, C8Defaults.DEFAULT_RESPONSE_SIZE_LIMIT));
    }

    private static String loadUser(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_USER, currentValue, C8Defaults.DEFAULT_USER);
    }

    private static String loadJWTToken(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_JWT_AUTH, currentValue, null);
    }

    private static String loadJWTUser(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_JWT_USER, currentValue, null);
    }

    private static String loadPassword(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_PASSWORD, currentValue, null);
    }

    private static String loadEmail(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_EMAIL, currentValue, null);
    }
    
    private static Boolean  loadJWTAuth(final Properties properties, final Boolean currentValue) {
        return Boolean
                .parseBoolean(getProperty(properties, PROPERTY_KEY_JWT_AUTH, currentValue, C8Defaults.DEFAULT_JWT_AUTH));
    }

    private static String loadApiKey(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_APIKEY, currentValue, null);
    }
    
    private static Boolean loadUseSsl(final Properties properties, final Boolean currentValue) {
        return Boolean
                .parseBoolean(getProperty(properties, PROPERTY_KEY_USE_SSL, currentValue, C8Defaults.DEFAULT_USE_SSL));
    }

    private static String loadhttpCookieSpec(final Properties properties, final String currentValue) {
        return getProperty(properties, PROPERTY_KEY_COOKIE_SPEC, currentValue, "");
    }

    private static Integer loadChunkSize(final Properties properties, final Integer currentValue) {
        return Integer.parseInt(getProperty(properties, PROPERTY_KEY_V_STREAM_CHUNK_CONTENT_SIZE, currentValue,
                C8Defaults.CHUNK_DEFAULT_CONTENT_SIZE));
    }

    private static Integer loadMaxConnections(final Properties properties, final Integer currentValue) {
        final String max = getProperty(properties, PROPERTY_KEY_MAX_CONNECTIONS, currentValue,
                null);
        return max != null ? Integer.parseInt(max) : null;
    }

    private static Long loadConnectionTtl(final Properties properties, final Long currentValue) {
        final String ttl = getProperty(properties, PROPERTY_KEY_CONNECTION_TTL, currentValue,
                C8Defaults.CONNECTION_TTL_VST_DEFAULT);
        return ttl != null ? Long.parseLong(ttl) : null;
    }

    private static Boolean loadAcquireHostList(final Properties properties, final Boolean currentValue) {
        return Boolean.parseBoolean(getProperty(properties, PROPERTY_KEY_ACQUIRE_HOST_LIST, currentValue,
                C8Defaults.DEFAULT_ACQUIRE_HOST_LIST));
    }

    private static int loadAcquireHostListInterval(final Properties properties, final Integer currentValue) {
        return Integer.parseInt(getProperty(properties, PROPERTY_KEY_ACQUIRE_HOST_LIST_INTERVAL, currentValue,
                C8Defaults.DEFAULT_ACQUIRE_HOST_LIST_INTERVAL));
    }

    private static LoadBalancingStrategy loadLoadBalancingStrategy(final Properties properties,
            final LoadBalancingStrategy currentValue) {
        return LoadBalancingStrategy.valueOf(getProperty(properties, PROPERTY_KEY_LOAD_BALANCING_STRATEGY, currentValue,
                C8Defaults.DEFAULT_LOAD_BALANCING_STRATEGY).toUpperCase());
    }

    private static Integer loadRetryTimeout(final Properties properties, final Integer currentValue) {
        return Integer
                .parseInt(getProperty(properties, PROPERTY_KEY_RETRY_TIMEOUT, currentValue,
                        C8Defaults.DEFAULT_RETRY_TIMEOUT));
    }

    protected static <T> String getProperty(final Properties properties, final String key, final T currentValue,
            final T defaultValue) {

        String overrideDefaultValue = null;

        if (currentValue != null) {
            overrideDefaultValue = currentValue.toString();
        } else if (defaultValue != null) {
            overrideDefaultValue = defaultValue.toString();
        }

        return properties.getProperty(key, overrideDefaultValue);
    }

    protected Map<Service, Collection<Host>> createHostMatrix(final int maxConnections,
                                                              final ConnectionFactory connectionFactory) {
        final Map matrix = new HashMap();

        for (Service service : Service.values()) {
            final Collection<Host> hostList = new ArrayList<>();
            for (final HostDescription host : hosts.get(service)) {
                hostList.add(HostUtils.createHost(host, maxConnections, connectionFactory, service));
            }
            matrix.put(service, hostList);
        }

        return matrix;
    }

}
