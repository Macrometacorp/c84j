/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8ApiKeys;
import com.c8db.entity.ApiKeyEntity;

public class C8ApiKeysImpl extends InternalC8ApiKeys<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8ApiKeys {

    protected C8ApiKeysImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public ApiKeyEntity validateApiKey(String apikey) {
	    return executor.execute(validateApiKeyRequest(apikey), validateApiKeyResponseDeserializer());
    }
}