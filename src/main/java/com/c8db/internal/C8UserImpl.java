/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8User;
import com.c8db.entity.StreamAccessLevel;

public class C8UserImpl extends InternalC8User<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8User {

    protected C8UserImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public StreamAccessLevel getStreamAccessLevel(String keyId, String stream) {
        return executor.execute(streamAccessLevelRequest(keyId, stream), streamAccessLevelResponseDeserializer());
    }
}
