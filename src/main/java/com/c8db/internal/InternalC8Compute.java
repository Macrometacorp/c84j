/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.FxEntity;
import com.c8db.entity.FxMetadataEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.model.FxReadOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8Compute<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
    extends C8Executeable<E> {

    protected static final String PATH_API_FX = "/_api/function";
    protected static final String METADATA = "metadata";
    protected static final String INVOKE = "invoke";

    private final D db;

    public InternalC8Compute(D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    protected Request getFunctionsRequest(final FxReadOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_FX);
        final FxReadOptions params = (options != null ? options : new FxReadOptions());
        request.putQueryParam("type", params.getType().toString().toLowerCase());
        return request;
    }

    protected ResponseDeserializer<Collection<FxEntity>> getFunctionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<FxEntity>>() {
            @Override
            public Collection<FxEntity> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                return util().deserialize(result, new Type<Collection<FxEntity>>() {
                }.getType());
            }
        };
    }

    protected Request getInfoRequest(final String name) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_FX, name);
        return request;
    }

    protected ResponseDeserializer<FxEntity> getInfoResponseDeserializer() {
        return new ResponseDeserializer<FxEntity>() {
            @Override
            public FxEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                Collection<FxEntity> entites = util().deserialize(result, new Type<Collection<FxEntity>>() {
                }.getType());
                return entites.isEmpty() ? null : entites.iterator().next();
            }
        };
    }

    protected Request getMetadataRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_FX, METADATA);
        return request;
    }

    protected ResponseDeserializer<FxMetadataEntity> getMetadataResponseDeserializer() {
        return new ResponseDeserializer<FxMetadataEntity>() {
            @Override
            public FxMetadataEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                return util().deserialize(result, new Type<FxMetadataEntity>() {
                }.getType());
            }
        };
    }

    protected Request executeFunctionRequest(String name, Map<String, Object> arguments) {
        final VPackSlice body = util().serialize(arguments);
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_FX, INVOKE, name);
        request.putQueryParam("params", body.toString());
        return request;
    }

    protected ResponseDeserializer<Collection<Object>> executeFunctionResponseDeserializer() {
        return new ResponseDeserializer<Collection<Object>>() {
            @Override
            public Collection<Object> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                if (result.isArray()) {
                    return util().deserialize(result, new Type<Collection<Object>>(){}.getType());
                } else {
                    Map<String, Object> map = util().deserialize(result, new Type<Map<String, Object>>(){}.getType());
                    return Collections.singletonList(map);
                }
            }
        };
    }

}
