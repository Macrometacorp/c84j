/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8ApiKeys;
import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.GeoFabricPermissions;
import com.c8db.entity.Permissions;

import java.util.Map;

public class C8ApiKeysImpl extends InternalC8ApiKeys<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8ApiKeys {

    protected C8ApiKeysImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public ApiKeyEntity validateApiKey(String apikey) {
	    return executor.execute(validateApiKeyRequest(apikey), validateApiKeyResponseDeserializer());
    }

    @Override
    public Map<String, GeoFabricPermissions> getResourcesAccess(final String keyId) {
        return executor.execute(geoFabricsAccessLevelRequest(keyId, true), gatResourcesAccessResponseDeserializer());
    }

    @Override
    public Permissions getGeoFabricAccess(String keyId) {
        return executor.execute(geoFabricAccessLevelRequest(keyId), streamAccessLevelResponseDeserializer());
    }

    @Override
    public Map<String, Permissions> getStreamsAccess(String keyId, boolean full) {
        return executor.execute(streamsAccessLevelRequest(keyId, full), listAccessesResponseDeserializer());
    }

    @Override
    public Permissions getStreamAccess(String keyId, String stream) {
        return executor.execute(streamAccessLevelRequest(keyId, stream), streamAccessLevelResponseDeserializer());
    }
}
