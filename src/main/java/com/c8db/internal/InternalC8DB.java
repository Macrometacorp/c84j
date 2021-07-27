/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.DataCenterEntity;
import com.c8db.entity.DcInfoEntity;
import com.c8db.entity.GeoFabricEntity;
import com.c8db.entity.LogLevelEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.ServerRole;
import com.c8db.entity.UserEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.DBCreateOptions;
import com.c8db.model.DCListOptions;
import com.c8db.model.LogOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.UserAccessOptions;
import com.c8db.model.UserCreateOptions;
import com.c8db.model.UserUpdateOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;


public abstract class InternalC8DB<E extends C8Executor> extends C8Executeable<E> {

    protected static final String PATH_API_USER = "/_admin/user";
    private static final String PATH_API_ADMIN_LOG = "/_admin/log";
    private static final String PATH_API_ADMIN_LOG_LEVEL = "/_admin/log/level";
    private static final String PATH_API_ROLE = "/_admin/server/role";

    protected InternalC8DB(final E executor, final C8SerializationFactory util, final C8Context context) {
        super(executor, util, context);
    }

    protected Request getRoleRequest() {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ROLE);
    }

    protected ResponseDeserializer<ServerRole> getRoleResponseDeserializer() {
        return new ResponseDeserializer<ServerRole>() {
            @Override
            public ServerRole deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("role"), ServerRole.class);
            }
        };
    }

    protected Request createGeoFabricRequest(final String tenant, final String name, final String spotDc, final String dcList, String geoFabric) {
        final Request request = request(tenant, name, RequestType.POST, InternalC8Database.PATH_API_DATABASE);
        request.setBody(util().serialize(OptionsBuilder.build(new DBCreateOptions(), tenant, geoFabric, spotDc, dcList)));
        return request;
    }

    protected ResponseDeserializer<Boolean> createGeoFabricResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().get(C8ResponseField.RESULT).getAsBoolean();
            }
        };
    }

    protected Request getGeoFabricsRequest(final String tenant, final String database) {
        return request(tenant, database, RequestType.GET, InternalC8Database.PATH_API_DATABASE);
    }

    protected ResponseDeserializer<Collection<String>> getGeoFabricsResponseDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Collection<String>>() {
                }.getType());
            }
        };
    }

    protected Request getAccessibleGeoFabricsForRequest(final String tenant, final String database, final String user) {
        return request(tenant, database, RequestType.GET, PATH_API_USER);
    }

    protected ResponseDeserializer<Collection<String>> getAccessibleGeoFabricsForResponseDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                final Collection<String> dbs = new ArrayList<String>();
                for (Iterator<VPackSlice> iterator = result.arrayIterator(); iterator
                        .hasNext();) {
                    for (Iterator<Entry<String, VPackSlice>> elements = iterator.next().objectIterator();elements.hasNext();) {
                        Entry<String, VPackSlice> element = elements.next();
                        if (element.getKey().equals("name")) {
                            dbs.add(element.getValue().getAsString());
                            break;
                        }
                    }
                }
                return dbs;
            }
        };
    }

    protected Request updateDCListRequest(final String tenant, final String name, final String dcList) {
        final Request request = request(tenant, C8RequestParam.SYSTEM, RequestType.POST,
                InternalC8Database.PATH_API_DATABASE, name, InternalC8Database.PATH_API_DCLIST);
        request.setBody(util().serialize(OptionsBuilder.build(new DCListOptions(), dcList)));
        return request;
    }

    protected ResponseDeserializer<Boolean> updateDCListResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().get(C8ResponseField.RESULT).getAsBoolean();
            }
        };
    }

    protected Request getGeoFabricInfoRequest(final String tenant, final String name) {
        return request(tenant, name, RequestType.GET, InternalC8Database.PATH_API_DATABASE, "current");
    }

    protected ResponseDeserializer<GeoFabricEntity> getGeoFabricInfoResponseDeserializer() {
        return new ResponseDeserializer<GeoFabricEntity>() {
            @Override
            public GeoFabricEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT), GeoFabricEntity.class);
            }
        };
    }

    protected Request updateSpotDcRequest(final String tenant, final String name, final String spotDc) {
        return request(tenant, name, RequestType.PUT, InternalC8Database.PATH_API_DATABASE, spotDc);
    }

    protected ResponseDeserializer<Boolean> updateSpotDcResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().get(C8ResponseField.RESULT).getAsBoolean();
            }
        };
    }

    protected Request getEdgeLocationsRequest(final String tenant) {
        return request(null, null, RequestType.GET, InternalC8Database.PATH_API_DCLIST,
                InternalC8Database.PATH_API_TENANT, tenant);
    }

    protected ResponseDeserializer<List<DataCenterEntity>> getEdgeLocationsResponseDeserializer() {
        return new ResponseDeserializer<List<DataCenterEntity>>() {
            @Override
            public List<DataCenterEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), new Type<ArrayList<DataCenterEntity>>() {
                }.getType());
            }
        };
    }

    protected Request getAllEdgeLocationsRequest() {
        return request(null, null, RequestType.GET, InternalC8Database.PATH_API_DCLIST, "all");
    }

    protected ResponseDeserializer<List<DcInfoEntity>> getAllEdgeLocationsResponseDeserializer() {
        return new ResponseDeserializer<List<DcInfoEntity>>() {
            @Override
            public List<DcInfoEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), new Type<ArrayList<DcInfoEntity>>() {
                }.getType());
            }
        };
    }

    protected Request getLocalEdgeLocationRequest() {
        return request(null, null, RequestType.GET, InternalC8Database.PATH_API_DCLIST, "local");
    }

    protected ResponseDeserializer<DcInfoEntity> getLocalEdgeLocationResponseDeserializer() {
        return new ResponseDeserializer<DcInfoEntity>() {
            @Override
            public DcInfoEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), DcInfoEntity.class);
            }
        };
    }

    protected Request getEdgeLocationRequest(final String dcName) {
        return request(null, null, RequestType.GET, InternalC8Database.PATH_API_DCLIST, dcName);
    }

    protected ResponseDeserializer<DcInfoEntity> getEdgeLocationResponseDeserializer() {
        return new ResponseDeserializer<DcInfoEntity>() {
            @Override
            public DcInfoEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), DcInfoEntity.class);
            }
        };
    }

    protected Request updateSpotStatusRequest(final String dcName, final boolean isSpot) {
        return request(null, null, RequestType.PUT, InternalC8Database.PATH_API_DCLIST, dcName,
                String.valueOf(isSpot));
    }

    protected ResponseDeserializer<Boolean> BooleanResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().getAsBoolean();
            }
        };
    }

    protected Request createUserRequest(final String tenant, final String database, final String user,
            final String passwd, final UserCreateOptions options) {
        final Request request;
        request = request(tenant, database, RequestType.POST, PATH_API_USER);
        request.setBody(util()
                .serialize(OptionsBuilder.build(options != null ? options : new UserCreateOptions(), user, passwd)));
        return request;
    }

    protected Request deleteUserRequest(final String tenant, final String database, final String user) {
        return request(tenant, database, RequestType.DELETE, PATH_API_USER, user);
    }

    protected Request getUsersRequest(final String tenant, final String database) {
        return request(tenant, database, RequestType.GET, PATH_API_USER);
    }

    protected Request getUserRequest(final String tenant, final String database, final String user) {
        return request(tenant, database, RequestType.GET, PATH_API_USER, user);
    }

    protected ResponseDeserializer<Collection<UserEntity>> getUsersResponseDeserializer() {
        return new ResponseDeserializer<Collection<UserEntity>>() {
            @Override
            public Collection<UserEntity> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Collection<UserEntity>>() {
                }.getType());
            }
        };
    }

    protected Request updateUserRequest(final String tenant, final String database, final String user,
            final UserUpdateOptions options) {
        final Request request;
        request = request(tenant, database, RequestType.PATCH, PATH_API_USER, user);
        request.setBody(util().serialize(options != null ? options : new UserUpdateOptions()));
        return request;
    }

    protected Request replaceUserRequest(final String tenant, final String database, final String user,
            final UserUpdateOptions options) {
        final Request request;
        request = request(tenant, database, RequestType.PUT, PATH_API_USER, user);
        request.setBody(util().serialize(options != null ? options : new UserUpdateOptions()));
        return request;
    }

    protected Request updateUserDefaultDatabaseAccessRequest(final String user, final Permissions permissions) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
                "*").setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
    }

    protected Request updateUserDefaultCollectionAccessRequest(final String user, final Permissions permissions) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
                "*", "*").setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
    }

    protected Request getLogsRequest(final LogOptions options) {
        final LogOptions params = options != null ? options : new LogOptions();
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ADMIN_LOG)
                .putQueryParam(LogOptions.PROPERTY_UPTO, params.getUpto())
                .putQueryParam(LogOptions.PROPERTY_LEVEL, params.getLevel())
                .putQueryParam(LogOptions.PROPERTY_START, params.getStart())
                .putQueryParam(LogOptions.PROPERTY_SIZE, params.getSize())
                .putQueryParam(LogOptions.PROPERTY_OFFSET, params.getOffset())
                .putQueryParam(LogOptions.PROPERTY_SEARCH, params.getSearch())
                .putQueryParam(LogOptions.PROPERTY_SORT, params.getSort());
    }

    protected Request getLogLevelRequest() {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ADMIN_LOG_LEVEL);
    }

    protected Request setLogLevelRequest(final LogLevelEntity entity) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_ADMIN_LOG_LEVEL)
                .setBody(util().serialize(entity));
    }

}
