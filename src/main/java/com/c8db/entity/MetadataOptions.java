/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MetadataOptions implements Entity {

    @SerializedName("associated_regions")
    private List<String> associatedRegions;
    private List<String> clusters;
    private String dcList;
    @SerializedName("dynamo_local_tables")
    private Boolean dynamoLocalTables;
    private Map<String, Object> metadata;
    private String name;
    private Boolean realTime;
    private String spotDc;
    private String status;
    private String tenant;

    /**
     * @return the name of the database
     */
    public String getName() {
        return name;
    }

    /**
     * @return list of associated regions
     */
    public List<String> getAssociatedRegions() {
        return associatedRegions;
    }

    /**
     * @return list of clusters
     */
    public List<String> getClusters() {
        return clusters;
    }

    /**
     * @return dc list as a string
     */
    public String getDcList() {
        return dcList;
    }

    /**
     * @return is local dynamo table
     */
    public Boolean getDynamoLocalTables() {
        return dynamoLocalTables;
    }

    /**
     * @return metadata of database
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * @return is real time
     */
    public Boolean getRealTime() {
        return realTime;
    }

    /**
     * @return DC spot
     */
    public String getSpotDc() {
        return spotDc;
    }

    /**
     * @return status of a database
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return tenant
     */
    public String getTenant() {
        return tenant;
    }
}
