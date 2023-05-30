/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8CEP;
import com.c8db.entity.C8StreamWorkerEntity;
import com.c8db.model.C8StreamWorkerOptions;

public class C8CEPImpl extends InternalC8StreamWorker<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8CEP {

    private static final String EMPTY = "";

    protected C8CEPImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public C8StreamWorkerEntity create(C8StreamWorkerOptions streamWorkerOptions) {
        return executor.execute(createStreamWorkerRequest(streamWorkerOptions),
                streamWorkerEntityResponseDeserializer());
    }

    @Override
    public C8StreamWorkerEntity get(String streamWorkerName) {
        return executor.execute(getStreamWorkerRequest(streamWorkerName, EMPTY, false),
                streamWorkerEntityResponseDeserializer());
    }

    @Override
    public C8StreamWorkerEntity get(String streamWorkerName, String onBehalfOfUser, boolean isSystem) {
        return executor.execute(getStreamWorkerRequest(streamWorkerName, onBehalfOfUser, isSystem),
                streamWorkerEntityResponseDeserializer());
    }

    @Override
    public C8StreamWorkerEntity update(String streamWorkerName, C8StreamWorkerOptions streamWorkerOptions) {
        return executor.execute(updateStreamWorkerRequest(streamWorkerName, streamWorkerOptions, EMPTY, false),
                streamWorkerEntityResponseDeserializer());
    }

    @Override
    public C8StreamWorkerEntity update(String streamWorkerName, C8StreamWorkerOptions streamWorkerOptions,
                                       String onBehalfOfUser, boolean isSystem) {
        return executor.execute(updateStreamWorkerRequest(streamWorkerName, streamWorkerOptions, onBehalfOfUser,
                isSystem), streamWorkerEntityResponseDeserializer());
    }

    @Override
    public void delete(String streamWorkerName) {
        executor.execute(deleteStreamWorkerRequest(streamWorkerName, EMPTY, false), Void.class);
    }

    @Override
    public void delete(String streamWorkerName, String onBehalfOfUser, boolean isSystem) {
        executor.execute(deleteStreamWorkerRequest(streamWorkerName, onBehalfOfUser, isSystem), Void.class);
    }

    @Override
    public C8StreamWorkerEntity activate(String streamWorkerName, Boolean isActive) {
        return executor.execute(activateStreamWorkerRequest(streamWorkerName, isActive, EMPTY, false),
                streamWorkerEntityResponseDeserializer());
    }

    @Override
    public C8StreamWorkerEntity activate(String streamWorkerName, Boolean isActive,
                                         String onBehalfOfUser, boolean isSystem) {
        return executor.execute(activateStreamWorkerRequest(streamWorkerName, isActive, onBehalfOfUser, isSystem),
                streamWorkerEntityResponseDeserializer());
    }

}
