/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.c8db.C8Admin;
import com.c8db.C8DBException;
import com.c8db.entity.*;
import com.c8db.model.TenantMetricsOption;
import com.c8db.util.C8Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class C8AdminImpl extends InternalC8Admin<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Admin {

    protected C8AdminImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public FeaturesEntity getTenantFeatures(String tenant) throws C8DBException {
        return executor.execute(getTenantFeaturesRequest(tenant), getTenantFeaturesResponseDeserializer());
    }

	@Override
	public LimitsEntity getTenantLimits(String tenant) throws C8DBException {
		return executor.execute(getTenantLimitsRequest(tenant), getTenantLimitsResponseDeserializer());
	}

	@Override
	public List<TenantsEntity> getTenants() throws C8DBException {
		return executor.execute(getTenantsRequest(), getTenantsResponseDeserializer());
	}

	@Override
	public TenantEntity getTenant(String tenant) throws C8DBException {
		return executor.execute(getTenantRequest(tenant), getTenantResponseDeserializer());
	}

	@Override
	public TenantMetricsEntity getTenantMetrics(String tenant, TenantMetricsOption options) throws C8DBException, JsonProcessingException {
		String result =  executor.execute(getTenantMetricsRequest(tenant,options),
				getTenantMetricResponseDeserializer());
		TenantMetricsEntity tenantMetrics = C8Mapper.mapTenantMetrics(result);
		return tenantMetrics;
	}
}
