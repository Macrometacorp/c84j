/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.ApiKeyCreateEntity;
import com.c8db.entity.ApiKeyJwtEntity;
import com.c8db.entity.GeoFabricPermissions;
import com.c8db.entity.Permissions;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.ApiKeyCreateOptions;
import com.c8db.model.ApiKeyOptions;
import com.c8db.model.JwtOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.UserAccessOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.Map;

import static com.c8db.internal.InternalC8Database.QUERY_PARAM_FULL;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8ApiKeys<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>,
        E extends C8Executor> extends C8Executeable<E> {

    protected static final String PATH_API_KEY_VALIDATE = "/_api/key/validate";
    protected static final String PATH_API_KEY = "/_api/key";
    public static final String SYSTEM_TENANT = "_mm";
    public static final String MM_ROOT = "_mm.root";
    public static final String ROOT = "root";

    private final D db;

    public InternalC8ApiKeys(D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    protected ResponseDeserializer<ApiKeyJwtEntity> validateApiKeyJwtResponseDeserializer() {
        return new ResponseDeserializer<ApiKeyJwtEntity>() {
            @Override
            public ApiKeyJwtEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<ApiKeyJwtEntity>(){}.getType());
            }
        };
    }

    protected Request validateApiKeyRequest(final String apikey) {
        final Request request = request(null, null, RequestType.POST, PATH_API_KEY_VALIDATE);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(
                OptionsBuilder.build(new ApiKeyOptions(), apikey)));
        return request;
    }

    protected Request validateJwtRequest(final String jwt) {
        final Request request = request(null, null, RequestType.POST, PATH_API_KEY_VALIDATE);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(
            OptionsBuilder.build(new JwtOptions(), jwt)));
        return request;
    }

    protected Request geoFabricsAccessLevelRequest(final String keyId, boolean full) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
                String.join(".", db.tenant(), keyId), C8RequestParam.DATABASE);
        request.putQueryParam(QUERY_PARAM_FULL, full);
        return request;
    }

    protected Request geoFabricAccessLevelRequest(final String keyId) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
                String.join(".", db.tenant(), keyId), C8RequestParam.DATABASE,
                String.join(".", db.tenant(), db.name()));
        return request;
    }

    protected Request streamsAccessLevelRequest(final String keyId, final boolean full) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
                String.join(".", db.tenant(), keyId), C8RequestParam.DATABASE,
                String.join(".", db.tenant(), db.name()), C8RequestParam.STREAM);
        if (full) {
            request.putQueryParam(QUERY_PARAM_FULL, true);
        }
        return request;
    }

    protected Request streamAccessLevelRequest(final String keyId, final String stream) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY,
                String.join(".", db.tenant(), keyId), C8RequestParam.DATABASE,
                String.join(".", db.tenant(), db.name()), C8RequestParam.STREAM, stream);
        return request;
    }

    protected Request createApiKeyRequest(final String keyId, String onBehalfOfUser, boolean isSystem) {
        String user = MM_ROOT.equalsIgnoreCase(onBehalfOfUser) ? ROOT : onBehalfOfUser;
        final Request request = request(null, null, RequestType.POST, PATH_API_KEY);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM)
                .serialize(new ApiKeyCreateOptions(keyId, user, isSystem)));
        return request;
    }

    protected Request deleteApiKeyRequest(final String keyId, String tenant) {
        String key = (SYSTEM_TENANT.equalsIgnoreCase(tenant)) ? keyId : String.join(".", tenant, keyId);
        return request(null, null, RequestType.DELETE, PATH_API_KEY, key);
    }

    protected Request grantDatabasePermissionRequest(final String keyId, final String tenant,
                                                     final String fabric, final Permissions permissions) {
        String key = (SYSTEM_TENANT.equalsIgnoreCase(tenant)) ? keyId : String.join(".", tenant, keyId);
        return request(null, null, RequestType.PUT, PATH_API_KEY, key, C8RequestParam.DATABASE,
                fabric).setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
    }

    protected ResponseDeserializer<Permissions> streamAccessLevelResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
            return util().deserialize(result, new Type<Permissions>() {
            }.getType());
        };
    }

    protected ResponseDeserializer<Map<String, GeoFabricPermissions>> gatResourcesAccessResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
            return util().deserialize(result, new Type<Map<String, GeoFabricPermissions>>() {
            }.getType());
        };
    }

    protected ResponseDeserializer<Map<String, Permissions>> listAccessesResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
            return util().deserialize(result, new Type<Map<String, Permissions>>() {
            }.getType());
        };
    }

    protected ResponseDeserializer<ApiKeyCreateEntity> createApiKeyResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody();
            return util().deserialize(result, new Type<ApiKeyCreateEntity>() {
            }.getType());
        };
    }

}
