/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.FeaturesEntity;

/**
 * Interface for operations on administration level.
 */
public interface C8Admin extends C8SerializationAccessor {

    /**
     * The the handler of the database
     *
     * @return database handler
     */
    C8Database db();

    /**
     * Fetches all features associated with given tenant
     *
     * @return all features for a given tenant
     * @throws C8DBException
     * @userName tenant
     */
    FeaturesEntity getTenantFeatures(final String tenant) throws C8DBException;

    // TODO: Implement other required admin features.

}
