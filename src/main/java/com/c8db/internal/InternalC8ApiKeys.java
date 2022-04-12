/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.Permissions;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.ApiKeyOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    protected Request geoFabricAccessLevelRequest(final String keyId) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY, keyId,
            C8RequestParam.DATABASE, db.name());
        return request;
    }

    protected Request streamsAccessLevelRequest(final String keyId, final boolean full) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY, keyId,
            C8RequestParam.DATABASE, db.name(), C8RequestParam.STREAM);
        if (full) {
            request.putQueryParam("full", true);
        }
        return request;
    }

    protected Request streamAccessLevelRequest(final String keyId, final String stream) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY, keyId,
            C8RequestParam.DATABASE, db.name(), C8RequestParam.STREAM, stream);
        return request;
    }

    protected ResponseDeserializer<Permissions> streamAccessLevelResponseDeserializer() {
        return new ResponseDeserializer<Permissions>() {
            @Override
            public Permissions deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                String level = util().deserialize(result,  new Type<String>(){}.getType());
                return Permissions.valueOf(level.toUpperCase());
            }
        };
    }

    protected ResponseDeserializer<Map<String, Permissions>> listAccessesResponseDeserializer() {
        return new ResponseDeserializer<Map<String, Permissions>>() {
            @Override
            public Map<String, Permissions> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                Map<String, Permissions> permissions = new HashMap<>();
                for (Iterator<Map.Entry<String, VPackSlice>> it = result.objectIterator(); it.hasNext();) {
                    Map.Entry<String, VPackSlice> next = it.next();
                    String level = util().deserialize(next.getValue(),  new Type<String>(){}.getType());
                    Permissions permission = null;
                    try {
                        permission = Permissions.valueOf(level.toUpperCase());
                    } catch (Exception e) {
                        // has level "undefined"
                    }
                    permissions.put(next.getKey(), permission);
                }
                return permissions;
            }
        };
    }

}
