/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.c8db.entity.GeoFabricPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DB;
import com.c8db.C8DBException;
import com.c8db.C8Database;
import com.c8db.Protocol;
import com.c8db.entity.C8DBVersion;
import com.c8db.entity.DataCenterEntity;
import com.c8db.entity.DcInfoEntity;
import com.c8db.entity.GeoFabricEntity;
import com.c8db.entity.LogEntity;
import com.c8db.entity.LogLevelEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.ServerRole;
import com.c8db.entity.UserEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.http.HttpCommunication;
import com.c8db.internal.http.HttpProtocol;
import com.c8db.internal.net.CommunicationProtocol;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.net.HostResolver;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.internal.velocystream.VstCommunicationSync;
import com.c8db.internal.velocystream.VstProtocol;
import com.c8db.model.LogOptions;
import com.c8db.model.UserCreateOptions;
import com.c8db.model.UserUpdateOptions;
import com.c8db.util.C8CursorInitializer;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 *
 */
public class C8DBImpl extends InternalC8DB<C8ExecutorSync> implements C8DB {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8DBImpl.class);

    private C8CursorInitializer cursorInitializer;
    private CommunicationProtocol cp;

    public C8DBImpl(final VstCommunicationSync.Builder vstBuilder, final HttpCommunication.Builder httpBuilder,
            final C8SerializationFactory util, final Protocol protocol, final HostResolver hostResolver,
            final C8Context context) {

        super(new C8ExecutorSync(createProtocol(vstBuilder, httpBuilder, util.get(Serializer.INTERNAL), protocol),
                util, new DocumentCache()), util, context);

        cp = createProtocol(new VstCommunicationSync.Builder(vstBuilder).maxConnections(1),
                new HttpCommunication.Builder(httpBuilder), util.get(Serializer.INTERNAL), protocol);

        hostResolver.init(this.executor(), util());

        LOGGER.debug("ArangoDB Client is ready to use");

    }

    private static CommunicationProtocol createProtocol(final VstCommunicationSync.Builder vstBuilder,
            final HttpCommunication.Builder httpBuilder, final C8Serialization util, final Protocol protocol) {

        return (protocol == null || Protocol.VST == protocol) ? createVST(vstBuilder, util)
                : createHTTP(httpBuilder, util);
    }

    private static CommunicationProtocol createVST(final VstCommunicationSync.Builder builder,
            final C8Serialization util) {
        return new VstProtocol(builder.build(util));
    }

    private static CommunicationProtocol createHTTP(final HttpCommunication.Builder builder,
            final C8Serialization util) {
        return new HttpProtocol(builder.build(util));
    }

    @Override
    protected C8ExecutorSync executor() {
        return executor;
    }

    @Override
    public void shutdown() throws C8DBException {
        try {
            executor.disconnect();
            cp.close();
        } catch (final IOException e) {
            throw new C8DBException(e);
        }
    }

    @Override
    public C8Database db() {
        return db(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, "", "");
    }

    @Override
    public C8Database db(String tenant, String name) {
        return db(tenant, name, "", "");
    }

    @Override
    public C8Database db(final String tenant, final String name, final String spotDc, final String dcList) {
        return new C8DatabaseImpl(this, tenant, name, spotDc, dcList).setCursorInitializer(cursorInitializer);
    }

    @Override
    public C8Database db(String tenant, String name, Map<String, String> headerParams) {
        this.context.getHeaderParam().putAll(headerParams);
        return db(tenant, name, "", "");
    }
    
    @Override
    public Boolean createGeoFabric(final String tenant, final String name, final String spotDc, final String dcList, String geoFabric)
            throws C8DBException {
        return executor.execute(createGeoFabricRequest(tenant, name, spotDc, dcList, geoFabric), createGeoFabricResponseDeserializer());
    }

    @Override
    public Collection<String> getGeoFabrics() throws C8DBException {
        return executor.execute(getGeoFabricsRequest(db().tenant(), db().name()), getGeoFabricsResponseDeserializer());
    }

    @Override
    public Collection<String> getAccessibleGeoFabrics() throws C8DBException {
        return db().getAccessibleGeoFabrics();
    }

    @Override
    public Collection<String> getAccessibleGeoFabricsFor(final String user) throws C8DBException {
        return executor.execute(getAccessibleGeoFabricsForRequest(db().tenant(), db().name(), user, false),
                getAccessibleGeoFabricsForResponseDeserializer());
    }

    @Override
    public Boolean updateDataCentersForGeoFabric(final String tenant, final String name, final String dcList) throws C8DBException {
        return executor.execute(updateDCListRequest(tenant, name, dcList), updateDCListResponseDeserializer());
    }

    @Override
    public Boolean updateSpotRegionForGeoFabric(final String tenant, final String name, final String spotDc) throws C8DBException {
        return executor.execute(updateSpotDcRequest(tenant, name, spotDc), updateSpotDcResponseDeserializer());
    }

    @Override
    public GeoFabricEntity getGeoFabricInformation(final String tenant, final String name) throws C8DBException {
        return executor.execute(getGeoFabricInfoRequest(tenant, name), getGeoFabricInfoResponseDeserializer());
    }

    /** Edge Locations */
    @Override
    public List<DataCenterEntity> getEdgeLocations(final String tenant) throws C8DBException {
        return executor.execute(getEdgeLocationsRequest(tenant),
                getEdgeLocationsResponseDeserializer());
    }

    @Override
    public List<DcInfoEntity> getAllEdgeLocations() throws C8DBException {
        return executor.execute(getAllEdgeLocationsRequest(), getAllEdgeLocationsResponseDeserializer());
    }

    @Override
    public DcInfoEntity getLocalEdgeLocation() throws C8DBException {
        return executor.execute(getLocalEdgeLocationRequest(), getLocalEdgeLocationResponseDeserializer());
    }

    @Override
    public DcInfoEntity getEdgeLocation(String dcName) throws C8DBException {
        return executor.execute(getEdgeLocationRequest(dcName), getEdgeLocationResponseDeserializer());
    }
    
    @Override
    public Boolean updateSpotStatus(String dcName, boolean isSpot) {
        return executor.execute(updateSpotStatusRequest(dcName, isSpot), BooleanResponseDeserializer());
    }

    @Override
    public C8DBVersion getVersion() throws C8DBException {
        return db().getVersion();
    }

    @Override
    public ServerRole getRole() throws C8DBException {
        return executor.execute(getRoleRequest(), getRoleResponseDeserializer());
    }

    //TODO: probably delete this
    @Override
    public UserEntity createUser(final String user, final String passwd, final String email) throws C8DBException {
        return executor.execute(createUserRequest(db().tenant(), C8RequestParam.SYSTEM, user, passwd, email, new UserCreateOptions()),
                UserEntity.class);
    }

    @Override
    public UserEntity createUser(final String user, final String passwd, final String email, final UserCreateOptions options)
            throws C8DBException {
        return executor.execute(createUserRequest(db().tenant(), C8RequestParam.SYSTEM, user, passwd, email, options), UserEntity.class);
    }

    @Override
    public void deleteUser(final String user) throws C8DBException {
        executor.execute(deleteUserRequest(db().tenant(), C8RequestParam.SYSTEM, user), Void.class);
    }

    @Override
    public UserEntity getUser(final String user) throws C8DBException {
        return executor.execute(getUserRequest(db().tenant(), C8RequestParam.SYSTEM, user), UserEntity.class);
    }

    @Override
    public Collection<UserEntity> getUsers() throws C8DBException {
        return executor.execute(getUsersRequest(db().tenant(), C8RequestParam.SYSTEM), getUsersResponseDeserializer());
    }

    @Override
    public UserEntity updateUser(final String user, final UserUpdateOptions options) throws C8DBException {
        return executor.execute(updateUserRequest(db().tenant(), C8RequestParam.SYSTEM, user, options), UserEntity.class);
    }

    @Override
    public UserEntity replaceUser(final String user, final UserUpdateOptions options) throws C8DBException {
        return executor.execute(replaceUserRequest(db().tenant(), C8RequestParam.SYSTEM, user, options), UserEntity.class);
    }

    @Override
    public void grantDefaultDatabaseAccess(final String user, final Permissions permissions) throws C8DBException {
        executor.execute(updateUserDefaultDatabaseAccessRequest(user, permissions), Void.class);
    }

    @Override
    public void grantDefaultCollectionAccess(final String user, final Permissions permissions)
            throws C8DBException {
        executor.execute(updateUserDefaultCollectionAccessRequest(user, permissions), Void.class);
    }

    @Override
    public Response execute(final Request request) throws C8DBException {
        return executor.execute(request, new ResponseDeserializer<Response>() {
            @Override
            public Response deserialize(final Response response) throws VPackException {
                return response;
            }
        });
    }

    @Override
    public Response execute(final Request request, final HostHandle hostHandle) throws C8DBException {
        return executor.execute(request, new ResponseDeserializer<Response>() {
            @Override
            public Response deserialize(final Response response) throws VPackException {
                return response;
            }
        }, hostHandle);
    }

    @Override
    public LogEntity getLogs(final LogOptions options) throws C8DBException {
        return executor.execute(getLogsRequest(options), LogEntity.class);
    }

    @Override
    public LogLevelEntity getLogLevel() throws C8DBException {
        return executor.execute(getLogLevelRequest(), LogLevelEntity.class);
    }

    @Override
    public LogLevelEntity setLogLevel(final LogLevelEntity entity) throws C8DBException {
        return executor.execute(setLogLevelRequest(entity), LogLevelEntity.class);
    }

    @Override
    public C8DBImpl _setCursorInitializer(final C8CursorInitializer cursorInitializer) {
        this.cursorInitializer = cursorInitializer;
        return this;
    }

}
