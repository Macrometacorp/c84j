package com.c8db.entity;

import lombok.Data;

@Data
public class MetricsEntity implements Entity{
    private long timestamp;
    private String tenant;
    private String geofabric;
    private String appName;
    private String name;
    private String type;
    private String user;
    private String value;
}
