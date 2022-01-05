/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.*;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.TenantMetricsOption;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.List;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8Admin<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_TENANTS = "/_api/tenants";
    protected static final String PATH_API_TENANT = "/_api/tenant";
    protected static final String PATH_API_FEATURES = "/features";
    protected static final String PATH_API_LIMITS = "/limits";
    protected static final String PATH_TENANT = "tenant";
    protected static final String PATH_API_METRICS = "/_api/metrics/query";
    protected static final String PATH_QUERY = "query";
    private final D db;

    protected InternalC8Admin(final D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    public D db() {
        return db;
    }

    protected ResponseDeserializer<List<TenantsEntity>> getTenantsResponseDeserializer() {
        return new ResponseDeserializer<List<TenantsEntity>>() {
            @Override
            public List<TenantsEntity> deserialize(final Response response) throws VPackException {
            	 final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<List<TenantsEntity>>(){}.getType());
            }
        };
    }

    protected ResponseDeserializer<TenantEntity> getTenantResponseDeserializer() {
        return new ResponseDeserializer<TenantEntity>() {
            @Override
            public TenantEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<TenantEntity>(){}.getType());
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
        System.out.println("Building response....");
        return new ResponseDeserializer<LimitsEntity>() {

            @Override
            public LimitsEntity deserialize(final Response response) throws VPackException {
                System.out.println("Deserialzing...");
            	 final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<LimitsEntity>(){}.getType());
            }
        };
    }

    protected ResponseDeserializer<String> getTenantMetricResponseDeserializer() {
        return new ResponseDeserializer<String>() {
            @Override
            public String deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result,  new Type<String>(){}.getType());
            }
        };
    }

    protected Request getTenantsRequest() {
        return request(null, null, RequestType.GET, PATH_API_TENANTS);
    }

    protected Request getTenantRequest(final String tenant) {
        return request(null, null, RequestType.GET, PATH_API_TENANT, tenant);
    }
    
    protected Request getTenantLimitsRequest(final String tenant) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_LIMITS, PATH_TENANT, tenant);
    }
    
    protected Request getTenantFeaturesRequest(final String tenant) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_FEATURES, PATH_TENANT, tenant);
    }

    protected Request getTenantMetricsRequest(final String tenant, TenantMetricsOption options){
        //Finalize defaults of TenantMetricsOption with Stoyan
        VPackSlice body = util()
                .serialize(options);
        Request request= request(null,db.name(), RequestType.POST,PATH_API_METRICS).setBody(body);
        return request;
    }
}
