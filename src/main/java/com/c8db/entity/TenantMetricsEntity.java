package com.c8db.entity;
import lombok.Data;

import java.util.List;

@Data
public class TenantMetricsEntity implements Entity {
        List<MetricsEntity> throughput;
        List<MetricsEntity> latencySum;
        List<MetricsEntity> latencyCount;

        public TenantMetricsEntity(List<MetricsEntity> throughput, List<MetricsEntity> latencySum, List<MetricsEntity> latencyCount) {
                this.throughput = throughput;
                this.latencySum = latencySum;
                this.latencyCount = latencyCount;
        }
}
