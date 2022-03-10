/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */
package com.c8db;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackAnnotationFieldFilter;
import com.arangodb.velocypack.VPackAnnotationFieldNaming;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackInstanceCreator;
import com.arangodb.velocypack.VPackJsonDeserializer;
import com.arangodb.velocypack.VPackJsonSerializer;
import com.arangodb.velocypack.VPackModule;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackParserModule;
import com.arangodb.velocypack.VPackSerializer;
import com.arangodb.velocypack.ValueType;
import com.c8db.entity.C8DBVersion;
import com.c8db.entity.DataCenterEntity;
import com.c8db.entity.DcInfoEntity;
import com.c8db.entity.GeoFabricEntity;
import com.c8db.entity.LoadBalancingStrategy;
import com.c8db.entity.LogEntity;
import com.c8db.entity.LogLevelEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.ServerRole;
import com.c8db.entity.UserEntity;
import com.c8db.internal.C8Context;
import com.c8db.internal.C8DBImpl;
import com.c8db.internal.C8Defaults;
import com.c8db.internal.InternalC8DBBuilder;
import com.c8db.internal.http.HttpCommunication;
import com.c8db.internal.http.HttpConnectionFactory;
import com.c8db.internal.net.ConnectionFactory;
import com.c8db.internal.net.Host;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.net.HostHandler;
import com.c8db.internal.net.HostResolver;
import com.c8db.internal.util.C8DeserializerImpl;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.util.C8SerializerImpl;
import com.c8db.internal.util.DefaultC8Serialization;
import com.c8db.internal.velocystream.VstCommunicationSync;
import com.c8db.internal.velocystream.VstConnectionFactorySync;
import com.c8db.model.LogOptions;
import com.c8db.model.UserCreateOptions;
import com.c8db.model.UserUpdateOptions;
import com.c8db.util.C8CursorInitializer;
import com.c8db.util.C8Deserializer;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 * Central access point for applications to communicate with an C8DB server.
 *
 * <p>
 * Will be instantiated through {@link C8DB.Builder}
 * </p>
 *
 * <pre>
 * C8DB arango = new C8DB.Builder().build();
 * C8DB arango = new C8DB.Builder().host("127.0.0.1", 8529).build();
 * </pre>
 *
 */
public interface C8DB extends C8SerializationAccessor {

    /**
     * Builder class to build an instance of {@link C8DB}.
     *
     */
    public static class Builder extends InternalC8DBBuilder {

        private static String PROPERTY_KEY_PROTOCOL = "arangodb.protocol";

        protected Protocol protocol;

        public Builder() {
            super();
        }

        @Override
        protected void loadProperties(final Properties properties) {
            super.loadProperties(properties);
            protocol = loadProtocol(properties, protocol);
        }

        private static Protocol loadProtocol(final Properties properties, final Protocol currentValue) {
            return Protocol.valueOf(getProperty(properties, PROPERTY_KEY_PROTOCOL, currentValue,
                    C8Defaults.DEFAULT_NETWORK_PROTOCOL).toUpperCase());
        }

        public Builder useProtocol(final Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        @Override
        public Builder loadProperties(final InputStream in) throws C8DBException {
            super.loadProperties(in);
            return this;
        }

        /**
         * Adds a host to connect to. Multiple hosts can be added to provide fallbacks.
         *
         * @param host address of the host
         * @param port port of the host
         * @return {@link C8DB.Builder}
         */
        public Builder host(final String host, final int port) {
            setHost(host, port);
            return this;
        }

        /**
         * Sets the connection and request timeout in milliseconds.
         *
         * @param timeout timeout in milliseconds
         * @return {@link C8DB.Builder}
         */
        public Builder timeout(final Integer timeout) {
            setTimeout(timeout);
            return this;
        }

        /**
         * Sets the username to use for authentication.
         *
         * @param user the user in the database (default: {@code root})
         * @return {@link C8DB.Builder}
         */
        public Builder user(final String user) {
            setUser(user);
            return this;
        }

        /**
         * Sets the password for the user for authentication.
         *
         * @param password the password of the user in the database (default:
         *                 {@code null})
         * @return {@link C8DB.Builder}
         */
        public Builder password(final String password) {
            setPassword(password);
            return this;
        }

        public Builder email(final String email) {
            setEmail(email);
            return this;
        }

        public Builder jwtToken(final String jwt) {
            setJwtToken(jwt);
            return this;
        }

        public Builder apiKey(final String apiKey) {
            setApiKey(apiKey);
            return this;
        }

        /**
         * If set to {@code true} SSL will be used when connecting to an ArangoDB
         * server.
         *
         * @param useSsl whether or not use SSL (default: {@code false})
         * @return {@link C8DB.Builder}
         */
        public Builder useSsl(final Boolean useSsl) {
            setUseSsl(useSsl);
            return this;
        }

        /**
         * Sets the SSL context to be used when {@code true} is passed through
         * {@link #useSsl(Boolean)}.
         *
         * @param sslContext SSL context to be used
         * @return {@link C8DB.Builder}
         */
        public Builder sslContext(final SSLContext sslContext) {
            setSslContext(sslContext);
            return this;
        }

        /**
         * Sets the chunk size when {@link Protocol#VST} is used.
         *
         * @param chunksize size of a chunk in bytes
         * @return {@link C8DB.Builder}
         */
        public Builder chunksize(final Integer chunksize) {
            setChunksize(chunksize);
            return this;
        }

        /**
         * Sets the maximum number of connections the built in connection pool will open
         * per host.
         *
         * <p>
         * Defaults:
         * </p>
         *
         * <pre>
         * {@link Protocol#VST} == 1
         * {@link Protocol#HTTP_JSON} == 20
         * {@link Protocol#HTTP_VPACK} == 20
         * </pre>
         *
         * @param maxConnections max number of connections
         * @return {@link C8DB.Builder}
         */
        public Builder maxConnections(final Integer maxConnections) {
            setMaxConnections(maxConnections);
            return this;
        }

        /**
         * Set the maximum time to life of a connection. After this time the connection
         * will be closed automatically.
         *
         * @param connectionTtl the maximum time to life of a connection in milliseconds
         * @return {@link C8DB.Builder}
         */
        public Builder connectionTtl(final Long connectionTtl) {
            setConnectionTtl(connectionTtl);
            return this;
        }

        /**
         * Whether or not the driver should acquire a list of available coordinators in
         * an ArangoDB cluster or a single server with active failover.
         *
         * <p>
         * The host list will be used for failover and load balancing.
         * </p>
         *
         * @param acquireHostList whether or not automatically acquire a list of
         *                        available hosts (default: false)
         * @return {@link C8DB.Builder}
         */
        public Builder acquireHostList(final Boolean acquireHostList) {
            setAcquireHostList(acquireHostList);
            return this;
        }

        /**
         * Setting the Interval for acquireHostList
         *
         * @param acquireHostListInterval Interval in Seconds
         * 
         * @return {@link C8DB.Builder}
         */
        public Builder acquireHostListInterval(final Integer acquireHostListInterval) {
            setAcquireHostListInterval(acquireHostListInterval);
            return this;
        }

        /**
         * Sets the load balancing strategy to be used in an ArangoDB cluster setup.
         *
         * @param loadBalancingStrategy the load balancing strategy to be used (default:
         *                              {@link LoadBalancingStrategy#NONE}
         * @return {@link C8DB.Builder}
         */
        public Builder loadBalancingStrategy(final LoadBalancingStrategy loadBalancingStrategy) {
            setLoadBalancingStrategy(loadBalancingStrategy);
            return this;
        }

        /**
         * Register a custom {@link VPackSerializer} for a specific type to be used
         * within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param clazz      the type the serializer should be registered for
         * @param serializer serializer to register
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
            vpackBuilder.registerSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a special serializer for a member class which can only be identified
         * by its enclosing class.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param clazz      the type of the enclosing class
         * @param serializer serializer to register
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerEnclosingSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
            vpackBuilder.registerEnclosingSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackDeserializer} for a specific type to be used
         * within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param clazz        the type the serializer should be registered for
         * @param deserializer
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerDeserializer(final Class<T> clazz, final VPackDeserializer<T> deserializer) {
            vpackBuilder.registerDeserializer(clazz, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackInstanceCreator} for a specific type to be used
         * within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param clazz   the type the instance creator should be registered for
         * @param creator
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerInstanceCreator(final Class<T> clazz, final VPackInstanceCreator<T> creator) {
            vpackBuilder.registerInstanceCreator(clazz, creator);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonDeserializer} for a specific type to be
         * used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param type         the type the serializer should be registered for
         * @param deserializer
         * @return {@link C8DB.Builder}
         */
        public Builder registerJsonDeserializer(final ValueType type, final VPackJsonDeserializer deserializer) {
            vpackParserBuilder.registerDeserializer(type, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonDeserializer} for a specific type and
         * attribute name to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param attribute
         * @param type         the type the serializer should be registered for
         * @param deserializer
         * @return {@link C8DB.Builder}
         */
        public Builder registerJsonDeserializer(final String attribute, final ValueType type,
                final VPackJsonDeserializer deserializer) {
            vpackParserBuilder.registerDeserializer(attribute, type, deserializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonSerializer} for a specific type to be used
         * within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param clazz      the type the serializer should be registered for
         * @param serializer
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerJsonSerializer(final Class<T> clazz, final VPackJsonSerializer<T> serializer) {
            vpackParserBuilder.registerSerializer(clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackJsonSerializer} for a specific type and
         * attribute name to be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param attribute
         * @param clazz      the type the serializer should be registered for
         * @param serializer
         * @return {@link C8DB.Builder}
         */
        public <T> Builder registerJsonSerializer(final String attribute, final Class<T> clazz,
                final VPackJsonSerializer<T> serializer) {
            vpackParserBuilder.registerSerializer(attribute, clazz, serializer);
            return this;
        }

        /**
         * Register a custom {@link VPackAnnotationFieldFilter} for a specific type to
         * be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param type        the type the serializer should be registered for
         * @param fieldFilter
         * @return {@link C8DB.Builder}
         */
        public <A extends Annotation> Builder annotationFieldFilter(final Class<A> type,
                final VPackAnnotationFieldFilter<A> fieldFilter) {
            vpackBuilder.annotationFieldFilter(type, fieldFilter);
            return this;
        }

        /**
         * Register a custom {@link VPackAnnotationFieldNaming} for a specific type to
         * be used within the internal serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param type        the type the serializer should be registered for
         * @param fieldNaming
         * @return {@link C8DB.Builder}
         */
        public <A extends Annotation> Builder annotationFieldNaming(final Class<A> type,
                final VPackAnnotationFieldNaming<A> fieldNaming) {
            vpackBuilder.annotationFieldNaming(type, fieldNaming);
            return this;
        }

        /**
         * Register a {@link VPackModule} to be used within the internal serialization
         * process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param module module to register
         * @return {@link C8DB.Builder}
         */
        public Builder registerModule(final VPackModule module) {
            vpackBuilder.registerModule(module);
            return this;
        }

        /**
         * Register a list of {@link VPackModule} to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param modules modules to register
         * @return {@link C8DB.Builder}
         */
        public Builder registerModules(final VPackModule... modules) {
            vpackBuilder.registerModules(modules);
            return this;
        }

        /**
         * Register a {@link VPackParserModule} to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param module module to register
         * @return {@link C8DB.Builder}
         */
        public Builder registerJsonModule(final VPackParserModule module) {
            vpackParserBuilder.registerModule(module);
            return this;
        }

        /**
         * Register a list of {@link VPackParserModule} to be used within the internal
         * serialization process.
         *
         * <p>
         * <strong>Attention:</strong>can not be used together with
         * {@link #serializer(C8Serialization)}
         * </p>
         *
         * @param modules modules to register
         * @return {@link C8DB.Builder}
         */
        public Builder registerJsonModules(final VPackParserModule... modules) {
            vpackParserBuilder.registerModules(modules);
            return this;
        }

        /**
         * Replace the built-in serializer with the given serializer.
         *
         * <br />
         * <b>ATTENTION!:</b> Use at your own risk
         *
         * @param serializer custom serializer
         * @deprecated use {@link #serializer(C8Serialization)} instead
         * @return {@link C8DB.Builder}
         */
        @Deprecated
        public Builder setSerializer(final C8Serializer serializer) {
            serializer(serializer);
            return this;
        }

        /**
         * Replace the built-in deserializer with the given deserializer.
         *
         * <br />
         * <b>ATTENTION!:</b> Use at your own risk
         *
         * @param deserializer custom deserializer
         * @deprecated use {@link #serializer(C8Serialization)} instead
         * @return {@link C8DB.Builder}
         */
        @Deprecated
        public Builder setDeserializer(final C8Deserializer deserializer) {
            deserializer(deserializer);
            return this;
        }

        /**
         * Replace the built-in serializer/deserializer with the given one.
         *
         * <br />
         * <b>ATTENTION!:</b> Any registered custom serializer/deserializer or module
         * will be ignored.
         *
         * @param serialization custom serializer/deserializer
         * @return {@link C8DB.Builder}
         */
        public Builder serializer(final C8Serialization serialization) {
            setSerializer(serialization);
            return this;
        }

        /**
         * Returns an instance of {@link C8DB}.
         *
         * @return {@link C8DB}
         */
        public synchronized C8DB build() {
            if (hosts.isEmpty()) {
                hosts.add(host);
            }

            final VPack vpacker = vpackBuilder.serializeNullValues(false).build();
            final VPack vpackerNull = vpackBuilder.serializeNullValues(true).build();
            final VPackParser vpackParser = vpackParserBuilder.build();
            final C8Serializer serializerTemp = serializer != null ? serializer
                    : new C8SerializerImpl(vpacker, vpackerNull, vpackParser);
            final C8Deserializer deserializerTemp = deserializer != null ? deserializer
                    : new C8DeserializerImpl(vpackerNull, vpackParser);
            final DefaultC8Serialization internal = new DefaultC8Serialization(serializerTemp,
                    deserializerTemp);
            final C8Serialization custom = customSerializer != null ? customSerializer : internal;
            final C8SerializationFactory util = new C8SerializationFactory(internal, custom);

            int protocolMaxConnections = protocol == Protocol.VST ? C8Defaults.MAX_CONNECTIONS_VST_DEFAULT
                    : C8Defaults.MAX_CONNECTIONS_HTTP_DEFAULT;
            final int max = maxConnections != null ? Math.max(1, maxConnections) : protocolMaxConnections;

            final ConnectionFactory connectionFactory = (protocol == null || Protocol.VST == protocol)
                    ? new VstConnectionFactorySync(host, timeout, connectionTtl, useSsl, sslContext)
                    : new HttpConnectionFactory(timeout, user, password, email, jwtAuth, useSsl, sslContext, custom, protocol,
                            connectionTtl, httpCookieSpec, jwtToken, apiKey);

            final Collection<Host> hostList = createHostList(max, connectionFactory);
            final HostResolver hostResolver = createHostResolver(hostList, max, connectionFactory);
            final HostHandler hostHandler = createHostHandler(hostResolver);
            return new C8DBImpl(
                    new VstCommunicationSync.Builder(hostHandler).timeout(timeout).user(user).password(password)
                            .useSsl(useSsl).sslContext(sslContext).chunksize(chunksize).maxConnections(maxConnections)
                            .connectionTtl(connectionTtl),
                    new HttpCommunication.Builder(hostHandler), util, protocol, hostResolver, new C8Context());
        }

    }

    /**
     * Releases all connections to the server and clear the connection pool.
     *
     * @throws C8DBException
     */
    void shutdown() throws C8DBException;

    /**
     * Returns a {@code ArangoDatabase} instance for the {@code _system} database.
     *
     * @return database handler
     */
    C8Database db();

    /**
     * Returns a {@code ArangoDatabase} instance for the given database name.
     *
     * @param tenant Name of the tenant
     * @param name   Name of the database
     * @param spotDc The Edge Location (Datacenter) where on-spot operations for the
     *               given geofabric will be performed. By default a random
     *               datacenter is chosen from those which are capable.
     * @param dcList The list of Edge Locations (Datacenters) as a comma-separated
     *               string. The individual elements for this parameter are your
     *               Edge Location URL prefixes up to the first . character.
     * @return database handler
     */
    C8Database db(String tenant, String name, String spotDc, String dcList);

    /**
     * Returns a {@code ArangoDatabase} instance for the given database name and
     * tenant.
     *
     * @param tenant Name of the tenant
     * @param name   Name of the database
     * @return database handler
     */
    C8Database db(String tenant, String name);

    /**
     * Returns a {@code ArangoDatabase} instance for the given database name and
     * tenant.
     *
     * @param tenant Name of the tenant
     * @param name   Name of the database
     * @param headerParams HTTP header parameters
     * @return database handler
     */
    C8Database db(String tenant, String name, Map<String, String> headerParams);
    
    /**
     * Creates a new database with the given name.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Database/DatabaseManagement.html#create-database">API
     *      Documentation</a>
     * @param tenant Name of the tenant
     * @param name   Name of the database to create
     * @param spotDc The Edge Location (Datacenter) where on-spot operations for the
     *               given geofabric will be performed. By default a random
     *               datacenter is chosen from those which are capable.
     * @param dcList The list of Edge Locations (Datacenters) as a comma-separated
     *               string. The individual elements for this parameter are your
     *               Edge Location URL prefixes up to the first . character.
     * @param geoFabric The name of the new db/geofabric              
     * @return true if the database was created successfully.
     * @throws C8DBException
     */
    Boolean createGeoFabric(String tenant, String name, String spotDc, String dcList, String geoFabric) throws C8DBException;

    /**
     * Retrieves a list of all existing databases
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Database/DatabaseManagement.html#list-of-databases">API
     *      Documentation</a>
     * @return a list of all existing databases
     * @throws C8DBException
     */
    Collection<String> getGeoFabrics() throws C8DBException;

    /**
     * Retrieves a list of all databases the current user can access
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Database/DatabaseManagement.html#list-of-accessible-databases">API
     *      Documentation</a>
     * @return a list of all databases the current user can access
     * @throws C8DBException
     */
    Collection<String> getAccessibleGeoFabrics() throws C8DBException;

    /**
     * List available database to the specified user
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#list-the-databases-available-to-a-user">API
     *      Documentation</a>
     * @param user The name of the user for which you want to query the databases
     * @return list of database names which are available for the specified user
     * @throws C8DBException
     */
    Collection<String> getAccessibleGeoFabricsFor(String user) throws C8DBException;

    /**
     * Updated the data centers for the specified database
     * 
     * @param tenant Name of the tenant
     * @param name   Name of the database for change
     * @param dcList The full list of Datacenters to be added to the GeoFabric
     *               including the new datacenters to be added, as a string. The
     *               Datacenter list cannot be empty. Each edge location in the
     *               string should be separated from the previous one with a comma
     *               character (‘,’)
     * @return true if the operation was successful
     * @throws C8DBException
     */
    Boolean updateDataCentersForGeoFabric(final String tenant, final String name, final String dcList) throws C8DBException;

    /**
     * 
     * @param tenant Name of the tenant
     * @param name   Name of the database for change
     * @return Retrieves information about the current geo-fabric
     * @throws C8DBException
     */
    GeoFabricEntity getGeoFabricInformation(final String tenant, final String name) throws C8DBException;
    
    /**
     * Updates the edge location where on-spot operations will be performed.
     * 
     * @param tenant Name of the tenant
     * @param name   Name of the database for change
     * @param spotDc The Edge Location (Datacenter) where on-spot operations for the
     *               given geofabric will be performed. By default a random
     *               datacenter is chosen from those which are capable.
     * @return true if successful
     * @throws C8DBException
     */
    Boolean updateSpotRegionForGeoFabric(final String tenant, final String name, final String spotDc) throws C8DBException;

    /** ---------- Edge Locations ------------*/

    /**
     * Lists Edge Location (AKA Datacenter) details for specified tenant
     * 
     * @param tenant Name of the tenant
     * @return Edge Location (AKA Datacenter) details for specified tenant
     * @throws C8DBException
     */
    List<DataCenterEntity> getEdgeLocations(final String tenant) throws C8DBException;

    /**
     * Return a list of all Edge Locations (AKA Datacenters) deployed in the Macrometa Fabric.
     * @return all Edge Locations (AKA Datacenters) deployed in the Macrometa Fabric.
     * @throws C8DBException
     */
    List<DcInfoEntity> getAllEdgeLocations() throws C8DBException;

    /**
     * List details of Local Edge Location
     * 
     * @return details of Local Edge Location
     * @throws C8DBException
     */
    DcInfoEntity getLocalEdgeLocation() throws C8DBException;

    /**
     * Fetches data about the specified Edge Location.
     * 
     * @param dcName datacenter name for which you want details
     * @return the specified edge location details
     * @throws C8DBException
     */
    DcInfoEntity getEdgeLocation(String dcName) throws C8DBException;

    /**
     * Change whether an edge location (Datacenter) is capable of being on-spot.
     * 
     * @param dcName The target edge location
     * @param isSpot Whether the edge location is capable of being on-spot
     * @return
     */
    Boolean updateSpotStatus(String dcName, boolean isSpot);

    /**
     * Returns the server name and version number.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/MiscellaneousFunctions/index.html#return-server-version">API
     *      Documentation</a>
     * @return the server version, number
     * @throws C8DBException
     */
    C8DBVersion getVersion() throws C8DBException;

    /**
     * Returns the server role.
     *
     * @return the server role
     * @throws C8DBException
     */
    ServerRole getRole() throws C8DBException;

    /**
     * Create a new user. This user will not have access to any database. You need
     * permission to the _system database in order to execute this call.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#create-user">API
     *      Documentation</a>
     * @param user   The name of the user
     * @param passwd The user password
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity createUser(String user, String passwd) throws C8DBException;

    /**
     * Create a new user. This user will not have access to any database. You need
     * permission to the _system database in order to execute this call.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#create-user">API
     *      Documentation</a>
     * @param user    The name of the user
     * @param passwd  The user password
     * @param options Additional options, can be null
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity createUser(String user, String passwd, UserCreateOptions options) throws C8DBException;

    /**
     * Removes an existing user, identified by user. You need access to the _system
     * database.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#remove-user">API
     *      Documentation</a>
     * @param user The name of the user
     * @throws C8DBException
     */
    void deleteUser(String user) throws C8DBException;

    /**
     * Fetches data about the specified user. You can fetch information about
     * yourself or you need permission to the _system database in order to execute
     * this call.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#fetch-user">API
     *      Documentation</a>
     * @param user The name of the user
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity getUser(String user) throws C8DBException;

    /**
     * Fetches data about the specified user for a given tenant. You can fetch information about
     * yourself or you need permission to the _system database in order to execute
     * this call.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#fetch-user">API
     *      Documentation</a>
     * @param user The name of the user
     * @param tenant The tenant of the user
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity getUser(final String user, final String tenant) throws C8DBException;

    /**
     * Fetches data about all users. You can only execute this call if you have
     * access to the _system database.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#list-available-users">API
     *      Documentation</a>
     * @return informations about all users
     * @throws C8DBException
     */
    Collection<UserEntity> getUsers() throws C8DBException;

    /**
     * Partially updates the data of an existing user. The name of an existing user
     * must be specified in user. You can only change the password of your self. You
     * need access to the _system database to change the active flag.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#update-user">API
     *      Documentation</a>
     * @param user    The name of the user
     * @param options Properties of the user to be changed
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity updateUser(String user, UserUpdateOptions options) throws C8DBException;

    /**
     * Replaces the data of an existing user. The name of an existing user must be
     * specified in user. You can only change the password of your self. You need
     * access to the _system database to change the active flag.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#replace-user">API
     *      Documentation</a>
     * @param user    The name of the user
     * @param options Additional properties of the user, can be null
     * @return information about the user
     * @throws C8DBException
     */
    UserEntity replaceUser(String user, UserUpdateOptions options) throws C8DBException;

    /**
     * Sets the default access level for databases for the user {@code user}. You
     * need permission to the _system database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @since ArangoDB 3.2.0
     * @throws C8DBException
     */
    void grantDefaultDatabaseAccess(String user, Permissions permissions) throws C8DBException;

    /**
     * Sets the default access level for collections for the user {@code user}. You
     * need permission to the _system database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @since ArangoDB 3.2.0
     * @throws C8DBException
     */
    void grantDefaultCollectionAccess(String user, Permissions permissions) throws C8DBException;

    /**
     * Get the stream access level
     * @param user user name
     * @param stream stream name
     * @return result of access level. Possible results are `ro`, `rw`, `none`
     */
    Permissions getStreamAccess(final String user, final String tenant, String fabric, final String stream);

    /**
     * Generic Execute. Use this method to execute custom FOXX services.
     *
     * @param request VelocyStream request
     * @return VelocyStream response
     * @throws C8DBException
     */
    Response execute(Request request) throws C8DBException;

    /**
     * Generic Execute. Use this method to execute custom FOXX services.
     *
     * @param request    VelocyStream request
     * @param hostHandle Used to stick to a specific host when using
     *                   {@link LoadBalancingStrategy#ROUND_ROBIN}
     * @return VelocyStream response
     * @throws C8DBException
     */
    Response execute(Request request, HostHandle hostHandle) throws C8DBException;

    /**
     * Returns fatal, error, warning or info log messages from the server's global
     * log.
     *
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AdministrationAndMonitoring/index.html#read-global-logs-from-the-server">API
     *      Documentation</a>
     * @param options Additional options, can be null
     * @return the log messages
     * @throws C8DBException
     */
    LogEntity getLogs(LogOptions options) throws C8DBException;

    /**
     * Returns the server's current loglevel settings.
     *
     * @return the server's current loglevel settings
     * @since ArangoDB 3.1.0
     * @throws C8DBException
     */
    LogLevelEntity getLogLevel() throws C8DBException;

    /**
     * Modifies and returns the server's current loglevel settings.
     *
     * @param entity loglevel settings
     * @return the server's current loglevel settings
     * @since ArangoDB 3.1.0
     * @throws C8DBException
     */
    LogLevelEntity setLogLevel(LogLevelEntity entity) throws C8DBException;

    /**
     * <strong>Attention:</strong> Please do not use!
     *
     * @param cursorInitializer
     * @return ArangoDB
     */
    C8DB _setCursorInitializer(C8CursorInitializer cursorInitializer);


}
