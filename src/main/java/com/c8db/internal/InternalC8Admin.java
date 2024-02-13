/*
 * Copyright (c) 2021 - 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.entity.FeaturesEntity;
import com.c8db.entity.LimitsEntity;
import com.c8db.entity.TenantEntity;
import com.c8db.entity.TenantsEntity;
import com.c8db.entity.TenantMetricsEntity;
import com.c8db.entity.TenantMetricsEntity.MetricsEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.model.TenantMetricsOption;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.c8db.internal.InternalC8ApiKeys.SYSTEM_TENANT;

/**
 * Internal request/response related functions.
 */
public abstract class InternalC8Admin<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_TENANTS = "/_api/tenants";
    protected static final String PATH_API_TENANT = "/_api/tenant";
    protected static final String PATH_API_FEATURES = "/_api/features";
    protected static final String PATH_API_LIMITS = "/_api/limits";
    protected static final String PATH_ENABLE = "enable";
    protected static final String PATH_TENANT = "tenant";
    protected static final String PATH_API_METRICS = "/_api/metrics/query";
    protected static final String PATH_QUERY = "query";
    private static final String C8CEP_THROUGHPUT_TOTAL = "c8cep_app_throughput_total";
    private static final String C8CEP_LATENCY_SUM = "c8cep_app_latency_seconds_sum";
    private final D db;

    protected InternalC8Admin(final D db) {
        super(db.executor, db.util, db.context, db.tenant(), db.credentials());
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

    protected ResponseDeserializer<Boolean> getLimitsEnabledResponseDeserializer() {
        return response -> {
            final VPackSlice result = response.getBody().get(C8ResponseField.RESULT).get("value");
            return util().deserialize(result, Boolean.class);
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

    protected ResponseDeserializer<TenantMetricsEntity> getTenantMetricResponseDeserializer() {
        return new ResponseDeserializer<TenantMetricsEntity>() {
            @Override
            public TenantMetricsEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                String responseObj = util().deserialize(result,  new Type<String>(){}.getType());
                TenantMetricsEntity tenantMetrics = getTenantMetrics(responseObj);
                return tenantMetrics;
            }
        };
    }

    protected Request getTenantsRequest() {
        return request(null, null, RequestType.GET, PATH_API_TENANTS);
    }

    protected Request getTenantRequest(final String tenant) {
        return request(null, null, RequestType.GET, PATH_API_TENANT, tenant);
    }

    protected Request getLimitsEnabledRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_LIMITS, PATH_ENABLE);
    }
    
    protected Request getTenantLimitsRequest(final String tenant) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_LIMITS, PATH_TENANT, tenant);
    }
    
    protected Request getTenantFeaturesRequest(final String tenant) {
        final Request request = new Request(SYSTEM_TENANT, null, null, RequestType.GET, db().credentials(), true,
                createPath(PATH_API_FEATURES, PATH_TENANT, tenant));
        for (final Map.Entry<String, String> header : context.getHeaderParam().entrySet()) {
            request.putHeaderParam(header.getKey(), header.getValue());
        }
        return request;
    }

    protected Request getTenantMetricsRequest(TenantMetricsOption options){
        //TODO: Finalize default values
        VPackSlice body = util()
                .serialize(options);
        Request request= request(null,db.name(), RequestType.POST,PATH_API_METRICS).setBody(body);
        return request;
    }

    /**
     * This method returns metrics only for the requested metric types for a tenant
     * TODO : Return metric for all metric-types or a default metric type
     * @param response - the response of the mertic API
     * @return retuns the requested tenant metrics
     */
    private TenantMetricsEntity getTenantMetrics(String response){
        ObjectMapper mapper = new ObjectMapper();
        TenantMetricsEntity tenantMetrics = new TenantMetricsEntity();
        try {
            JsonNode json = mapper.readValue(response, JsonNode.class);
            if(json.has(C8CEP_THROUGHPUT_TOTAL)) {
                String throughtputJson = mapper.readValue(response, JsonNode.class)
                        .get(C8CEP_THROUGHPUT_TOTAL).toString();
                List<MetricsEntity> throughputlist = Arrays.asList(mapper.readValue(throughtputJson, MetricsEntity[].class));
                tenantMetrics.setThroughput(throughputlist);
            }
            if(json.has(C8CEP_LATENCY_SUM)) {
                String latencySumJson = mapper.readValue(response, JsonNode.class)
                        .get(C8CEP_LATENCY_SUM).toString();
                List<MetricsEntity> latencySumlist = Arrays.asList(mapper.readValue(latencySumJson, MetricsEntity[].class));
                tenantMetrics.setLatencySum(latencySumlist);
            }
        }catch (JsonProcessingException ex){
            throw new C8DBException("PerfTestClient-Mapping-Tenant-Metrics : Exception processing Json while mapping",ex);
        }
        return tenantMetrics;
    }
}
