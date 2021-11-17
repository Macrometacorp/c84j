/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import java.util.List;

import com.c8db.C8Admin;
import com.c8db.C8DBException;
import com.c8db.entity.ApiKeyEntity;
import com.c8db.entity.FeaturesEntity;
import com.c8db.entity.LimitsEntity;
import com.c8db.entity.TenantsEntity;
import com.c8db.entity.TenantEntity;

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
}
