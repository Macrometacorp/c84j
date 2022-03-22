/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.Permissions;

/**
 * Interface for operations on administration level.
 */
public interface C8ApiKeys extends C8SerializationAccessor {

    /**
     * Validates APIKey and returns its properties
     *
     * @param apikey The apiKey
     * @return The api key properties
     */
    ApiKeyEntity validateApiKey(final String apikey);

    /**
     * Get the GeoFabric access level
     * @param keyId key id of a stream. ApiKey has the next structure: tenant.keyId.hash
     * @return result of access level. Possible results are `ro`, `rw`, `none`
     */
    Permissions getGeoFabricAccess(final String keyId);

    /**
     * Get the stream access level
     * @param keyId key id of a stream. ApiKey has the next structure: tenant.keyId.hash
     * @param stream stream name
     * @return result of access level. Possible results are `ro`, `rw`, `none`
     */
    Permissions getStreamAccess(final String keyId, final String stream);

    // TODO: Implement other required apikeys features.

}
