package com.c8db.util;

import com.c8db.entity.MetricsEntity;
import com.c8db.entity.TenantMetricsEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class C8Mapper {
    private static final String C8CEP_THROUGHPUT_TOTAL = "c8cep_app_throughput_total";
    private static final String C8CEP_LATENCY_SUM = "c8cep_app_latency_seconds_sum";
    private static final String C8CEP_LATENCY_COUNT = "c8cep_app_latency_seconds_count";

    public static TenantMetricsEntity mapTenantMetrics(String result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String throughtputJson = mapper.readValue(result, JsonNode.class)
                .get(C8CEP_THROUGHPUT_TOTAL).toString();
        String latencySumJson = mapper.readValue(result, JsonNode.class)
                .get(C8CEP_LATENCY_SUM).toString();
        String latencyCountJson = mapper.readValue(result, JsonNode.class)
                .get(C8CEP_LATENCY_COUNT).toString();
        List<MetricsEntity> throughputlist = Arrays.asList(mapper.readValue(throughtputJson, MetricsEntity[].class));
        List<MetricsEntity> latencySumlist = Arrays.asList(mapper.readValue(latencySumJson, MetricsEntity[].class));
        List<MetricsEntity> latencyCountlist = Arrays.asList(mapper.readValue(latencyCountJson, MetricsEntity[].class));

        TenantMetricsEntity tenantMetrics = new TenantMetricsEntity(throughputlist,latencySumlist,latencyCountlist);
        return tenantMetrics;
    }
}
