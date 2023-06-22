/*
 * Copyright (c) 2021-2023 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8ApiKeys;
import com.c8db.entity.ApiKeyCreateEntity;
import com.c8db.entity.ApiKeyJwtEntity;
import com.c8db.entity.GeoFabricPermissions;
import com.c8db.entity.Permissions;

import java.util.Map;

public class C8ApiKeysImpl extends InternalC8ApiKeys<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8ApiKeys {

    protected C8ApiKeysImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public ApiKeyJwtEntity validateApiKey(String apikey) {
        return executor.execute(validateApiKeyRequest(apikey), validateApiKeyJwtResponseDeserializer());
    }

    @Override
    public ApiKeyJwtEntity validateJwt(String jwt) {
        return executor.execute(validateJwtRequest(jwt), validateApiKeyJwtResponseDeserializer());
    }

    @Override
    public Map<String, GeoFabricPermissions> getResourcesPermissions(final String keyId) {
        return executor.execute(geoFabricsAccessLevelRequest(keyId, true), gatResourcesAccessResponseDeserializer());
    }

    @Override
    public Permissions getGeoFabricPermissions(String keyId) {
        return executor.execute(geoFabricAccessLevelRequest(keyId), streamAccessLevelResponseDeserializer());
    }

    @Override
    public Map<String, Permissions> getStreamsPermissions(String keyId, boolean full) {
        return executor.execute(streamsAccessLevelRequest(keyId, full), listAccessesResponseDeserializer());
    }

    @Override
    public Permissions getStreamPermissions(String keyId, String stream) {
        return executor.execute(streamAccessLevelRequest(keyId, stream), streamAccessLevelResponseDeserializer());
    }

    @Override
    public ApiKeyCreateEntity createApiKey(final String keyId) {
        return executor.execute(createApiKeyRequest(keyId, null, false), createApiKeyResponseDeserializer());
    }

    @Override
    public ApiKeyCreateEntity createApiKey(String keyId, String onBehalfOfUser, boolean isSystem) {
        return executor.execute(createApiKeyRequest(keyId, onBehalfOfUser, isSystem), createApiKeyResponseDeserializer());
    }

    @Override
    public void deleteApiKey(final String keyId) {
        executor.execute(deleteApiKeyRequest(keyId, null), Void.class);
    }

    @Override
    public void deleteApiKey(String keyId, String tenant) {
        executor.execute(deleteApiKeyRequest(keyId, tenant), Void.class);
    }

    @Override
    public void grantDatabasePermissions(String keyId, String tenant, String fabric, Permissions permissions) {
        executor.execute(grantDatabasePermissionRequest(keyId, tenant, fabric, permissions), Void.class);
    }

}
