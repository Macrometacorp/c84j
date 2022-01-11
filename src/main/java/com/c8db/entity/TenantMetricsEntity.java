/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */
package com.c8db.entity;
import lombok.Data;

import java.util.List;

@Data
public class TenantMetricsEntity implements Entity {
        List<MetricsEntity> throughput;
        List<MetricsEntity> latencySum;

        @Data
        public static class MetricsEntity{
                private long timestamp;
                private String tenant;
                private String geofabric;
                private String appName;
                private String name;
                private String type;
                private String user;
                private String value;
        }
}
