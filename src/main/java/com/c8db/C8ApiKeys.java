/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.ApiKeyEntity;

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

    // TODO: Implement other required admin features.

}
