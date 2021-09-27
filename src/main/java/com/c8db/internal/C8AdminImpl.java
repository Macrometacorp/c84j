/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8Admin;
import com.c8db.C8DBException;
import com.c8db.entity.FeaturesEntity;

public class C8AdminImpl extends InternalC8Admin<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Admin {

    protected C8AdminImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public FeaturesEntity getTenantFeatures(String tenant) throws C8DBException {
        return executor.execute(getTenantFeaturesRequest(tenant), getTenantFeaturesResponseDeserializer());
    }

}
