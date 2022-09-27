/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;

public class FxEntity implements Entity {

    @SerializedName("_id")
    private String id;
    @SerializedName("_key")
    private String key;
    @SerializedName("_rev")
    private String rev;
    private String activationStatus;
    private long createdAt;
    private long edgeWorkerId;
    private String environment;
    private String fabric;
    private long lastModified;
    private String name;
    private String queryWorkerName;
    private String queue;
    private FxType type;
    private String url;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getRev() {
        return rev;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getEdgeWorkerId() {
        return edgeWorkerId;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getFabric() {
        return fabric;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public String getQueryWorkerName() {
        return queryWorkerName;
    }

    public String getQueue() {
        return queue;
    }

    public FxType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
