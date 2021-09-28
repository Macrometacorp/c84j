/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;

import com.c8db.entity.FeaturesEntity;
import com.c8db.entity.LimitsEntity;

/**
 * Interface for operations on administration level.
 */
public interface C8Admin extends C8SerializationAccessor {

    /**
     * The handler of the database
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
    
    /**
     * Returns the the tenant limits
     * 
     * @param tenant The tenant name
     * @return All limits for this tenant
     * @throws C8DBException
     */
    LimitsEntity getTenantLimits(final String tenant) throws C8DBException;

    // TODO: Implement other required admin features.

}
