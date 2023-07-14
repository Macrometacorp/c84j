/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.c8db.entity.C8SecretEntity;
import com.c8db.entity.C8StreamWorkerEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.C8SecretOptions;
import com.c8db.model.C8StreamWorkerOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8Secret<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>,
        E extends C8Executor> extends C8Executeable<E> {

    private static final String PATH_API_SECRET = "/_api/kms/secret";
    private static final String PARAM_IS_SYSTEM = "isSystem";
    private final D db;

    public InternalC8Secret(D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    protected ResponseDeserializer<C8SecretEntity> secretEntityResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get("result");
            return util().deserialize(result, new Type<C8SecretEntity>() {
            }.getType());
        };
    }

    protected Request createSecretRequest(final C8SecretOptions secretOptions) {
        final Request request = request(null, db.name(), RequestType.POST, PATH_API_SECRET);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(secretOptions));
        return request;
    }

    protected Request getSecretRequest(final String name) {
        final Request request = request(null, db.name(), RequestType.GET, PATH_API_SECRET,
                name);
        return request;
    }

    protected Request updateSecretRequest(final C8SecretOptions secretOptions) {
        final Request request = request(null, db.name(), RequestType.PUT,
            PATH_API_SECRET, secretOptions.getName());
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(secretOptions));
        return request;
    }

    protected Request deleteSecretRequest(final String name) {
        final Request request = request(null, db.name(), RequestType.DELETE, PATH_API_SECRET,
            name);
        return request;
    }

}
