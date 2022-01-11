/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db;


import com.c8db.entity.FeaturesEntity;
import com.c8db.entity.LimitsEntity;
import com.c8db.entity.TenantEntity;
import com.c8db.entity.TenantsEntity;
import com.c8db.entity.TenantMetricsEntity;
import com.c8db.model.TenantMetricsOption;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

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
     * Returns the tenant limits
     * 
     * @return All tenants 
     * @throws C8DBException
     */
    List<TenantsEntity> getTenants() throws C8DBException;

    /**
     * Returns the requested tenant
     *
     * @param tenant The tenant name
     * @return The tenant matching the given tenant name
     * @throws C8DBException
     */
    TenantEntity getTenant(final String tenant) throws C8DBException;

    
    /**
     * Returns the tenant limits
     * 
     * @param tenant The tenant name
     * @return All limits for this tenant
     * @throws C8DBException
     */
    LimitsEntity getTenantLimits(final String tenant) throws C8DBException;

    /**
     * Returns metrics for a tenant
     *  @param options The parameters passed as a part of request body
     *  @return metrics for this tenant
     *  @throws C8DBException
     */
    TenantMetricsEntity getTenantMetrics(final TenantMetricsOption options) throws C8DBException;

    // TODO: Implement other required admin features.

}
