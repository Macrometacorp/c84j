/*
 * Copyright (c) 2023 - 2024 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.c8db.entity.C8StreamWorkerEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.C8StreamWorkerOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8StreamWorker<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>,
        E extends C8Executor> extends C8Executeable<E> {

    private static final String PATH_API_STREAM_APPS = "/_api/streamapps";
    private static final String PARAM_ACTIVE = "active";
    private static final String PARAM_USER = "user";
    private static final String PARAM_IS_SYSTEM = "isSystem";
    private final D db;

    public InternalC8StreamWorker(D db) {
        super(db.executor, db.util, db.context, db.tenant(), db.credentials());
        this.db = db;
    }

    protected ResponseDeserializer<C8StreamWorkerEntity> streamWorkerEntityResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get("streamApps").get(0);
            return util().deserialize(result, new Type<C8StreamWorkerEntity>() {
            }.getType());
        };
    }

    protected Request createStreamWorkerRequest(final C8StreamWorkerOptions streamWorkerOptions) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAM_APPS);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(streamWorkerOptions));
        return request;
    }

    protected Request getStreamWorkerRequest(final String streamWorkerName,
                                             final String onBehalfOfUser, final boolean isSystem) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAM_APPS,
                streamWorkerName);
        request.putQueryParam(PARAM_USER, onBehalfOfUser);
        request.putQueryParam(PARAM_IS_SYSTEM, isSystem);
        return request;
    }

    protected Request updateStreamWorkerRequest(final String streamWorkerName,
                                                final C8StreamWorkerOptions streamWorkerOptions,
                                                final String onBehalfOfUser, final boolean isSystem) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT,
                PATH_API_STREAM_APPS, streamWorkerName);
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(streamWorkerOptions));
        request.putQueryParam(PARAM_USER, onBehalfOfUser);
        request.putQueryParam(PARAM_IS_SYSTEM, isSystem);
        return request;
    }

    protected Request deleteStreamWorkerRequest(final String streamWorkerName,
                                                final String onBehalfOfUser, final boolean isSystem) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_STREAM_APPS,
                streamWorkerName);
        request.putQueryParam(PARAM_USER, onBehalfOfUser);
        request.putQueryParam(PARAM_IS_SYSTEM, isSystem);
        return request;
    }

    protected Request activateStreamWorkerRequest(final String streamWorkerName, final boolean isActive,
                                                  final String onBehalfOfUser, final boolean isSystem) {
        final Request request = request(db.tenant(), db.name(), RequestType.PATCH, PATH_API_STREAM_APPS,
                streamWorkerName, PARAM_ACTIVE);
        request.putQueryParam(PARAM_ACTIVE, isActive);
        request.putQueryParam(PARAM_USER, onBehalfOfUser);
        request.putQueryParam(PARAM_IS_SYSTEM, isSystem);
        return request;
    }

}
