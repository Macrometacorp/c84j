/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.Permissions;

import java.util.Map;

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
     * @return result of access level.
     */
    Permissions getGeoFabricAccess(final String keyId);

    /**
     * Get access level for streams
     * @param keyId key id of a stream. ApiKey has the next structure: tenant.keyId.hash
     * @param full Return the full set of access levels for all streams. If set to false, return the read-only streams.
     * @return result map of streams with access levels.
     */
    Map<String, Permissions> getStreamsAccess(final String keyId, final boolean full);

    /**
     * Get the stream access level
     * @param keyId key id of a stream. ApiKey has the next structure: tenant.keyId.hash
     * @param stream stream name
     * @return result of access level.
     */
    Permissions getStreamAccess(final String keyId, final String stream);

    // TODO: Implement other required apikeys features.

}
