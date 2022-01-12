/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */
package com.c8db.model;

import java.util.List;

public class TenantMetricsOption {
    private String tenant;
    private String geofabric;
    private long limit;
    private List<String> metrics;
    private String querytype;

    public TenantMetricsOption(){
        super();
    }

    public TenantMetricsOption(String tenant, String geofabric, long limit, List<String> metrics, String querytype) {
        this.tenant = tenant;
        this.geofabric = geofabric;
        this.limit = limit;
        this.metrics = metrics;
        this.querytype = querytype;
    }

    public TenantMetricsOption tenant(final String tenant) {
        this.tenant = tenant;
        return this;
    }
}
