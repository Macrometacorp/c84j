/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import java.util.List;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.FeaturesEntity;
import com.c8db.entity.LimitsEntity;
import com.c8db.entity.TenantEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8Admin<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_TENANTS = "/_api/tenants";
    protected static final String PATH_API_FEATURES = "/features";
    protected static final String PATH_API_LIMITS = "/limits";
    protected static final String PATH_TENANT = "tenant";
    private final D db;

    protected InternalC8Admin(final D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    public D db() {
        return db;
    }

    protected ResponseDeserializer<List<TenantEntity>> getTenantsResponseDeserializer() {
        return new ResponseDeserializer<List<TenantEntity>>() {
            @Override
            public List<TenantEntity> deserialize(final Response response) throws VPackException {
            	 final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<List<TenantEntity>>(){}.getType());
            }
        };
    }    
    
    protected ResponseDeserializer<FeaturesEntity> getTenantFeaturesResponseDeserializer() {
        return new ResponseDeserializer<FeaturesEntity>() {
            @Override
            public FeaturesEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), FeaturesEntity.class);
            }
        };
    }

    protected ResponseDeserializer<LimitsEntity> getTenantLimitsResponseDeserializer() {
        return new ResponseDeserializer<LimitsEntity>() {
            @Override
            public LimitsEntity deserialize(final Response response) throws VPackException {
            	 final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<LimitsEntity>(){}.getType());
            }
        };
    }
    
    protected Request getTenantsRequest() {
        return request(null, null, RequestType.GET, PATH_API_TENANTS);
    }
    
    protected Request getTenantLimitsRequest(final String tenant) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_LIMITS, PATH_TENANT, tenant);
    }
    
    protected Request getTenantFeaturesRequest(final String tenant) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_FEATURES, PATH_TENANT, tenant);
    }

}
