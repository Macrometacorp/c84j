/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.GeoFabricPermissions;
import com.c8db.entity.Permissions;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.ApiKeyOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.Map;

import static com.c8db.internal.InternalC8Database.QUERY_PARAM_FULL;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8ApiKeys<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_KEY_VALIDATE = "/_api/key/validate";
    protected static final String PATH_API_KEY = "/_api/key";

    private final D db;

    public InternalC8ApiKeys(D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    protected ResponseDeserializer<ApiKeyEntity> validateApiKeyResponseDeserializer() {
        return new ResponseDeserializer<ApiKeyEntity>() {
            @Override
            public ApiKeyEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<ApiKeyEntity>(){}.getType());
            }
        };
    }

    protected Request validateApiKeyRequest(final String apikey) {
        final Request request = request(null, null, RequestType.POST, PATH_API_KEY_VALIDATE);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(
                OptionsBuilder.build(new ApiKeyOptions(), apikey)));
        return request;
    }

    protected Request geoFabricsAccessLevelRequest(final String keyId, boolean full) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
            String.join("." ,db.tenant(), keyId), C8RequestParam.DATABASE);
        request.putQueryParam(QUERY_PARAM_FULL, full);
        return request;
    }

    protected Request geoFabricAccessLevelRequest(final String keyId) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
            String.join("." ,db.tenant(), keyId), C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()));
        return request;
    }

    protected Request streamsAccessLevelRequest(final String keyId, final boolean full) {
        final Request request = request(null, null, RequestType.GET, PATH_API_KEY,
            String.join("." ,db.tenant(), keyId), C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()), C8RequestParam.STREAM);
        if (full) {
            request.putQueryParam(QUERY_PARAM_FULL, true);
        }
        return request;
    }

    protected Request streamAccessLevelRequest(final String keyId, final String stream) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY,
            String.join("." ,db.tenant(), keyId), C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()), C8RequestParam.STREAM, stream);
        return request;
    }

    protected ResponseDeserializer<Permissions> streamAccessLevelResponseDeserializer() {
        return new ResponseDeserializer<Permissions>() {
            @Override
            public Permissions deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<Permissions>(){}.getType());
            }
        };
    }

    protected ResponseDeserializer<Map<String, GeoFabricPermissions>> gatResourcesAccessResponseDeserializer() {
        return new ResponseDeserializer<Map<String, GeoFabricPermissions>>() {
            @Override
            public Map<String, GeoFabricPermissions> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Map<String, GeoFabricPermissions>>(){}.getType());
            }
        };
    }

    protected ResponseDeserializer<Map<String, Permissions>> listAccessesResponseDeserializer() {
        return new ResponseDeserializer<Map<String, Permissions>>() {
            @Override
            public Map<String, Permissions> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Map<String, Permissions>>(){}.getType());
            }
        };
    }

}
