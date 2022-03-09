/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.ApiKeyEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.ApiKeyOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

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

    protected ResponseDeserializer<String> streamAccessLevelResponseDeserializer() {
        return new ResponseDeserializer<String>() {
            @Override
            public String deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<String>(){}.getType());
            }
        };
    }

    protected Request streamAccessLevelRequest(final String keyId, final String stream) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KEY, keyId,
            "database", db.name(), "stream", stream);
        return request;
    }

    }
