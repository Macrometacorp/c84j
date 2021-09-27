/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;

/**
 *
 */
public class FeaturesEntity implements Entity {

    @SerializedName("GEO_FABRICS")
    private Boolean geoFabricsEnabled;
    @SerializedName("GRAPHS")
    private Boolean graphsEnabled;
    @SerializedName("STREAMS")
    private Boolean streamsEnabled;
    @SerializedName("DYNAMO")
    private Boolean dynamoEnabled;
    @SerializedName("SEARCH")
    private Boolean searchEnabled;
    @SerializedName("CEP")
    private Boolean cepEnabled;
    @SerializedName("KV")
    private Boolean kvEnabled;
    @SerializedName("DOCS")
    private Boolean docsEnabled;
    @SerializedName("USERS")
    private Boolean usersEnabled;
    @SerializedName("COMPUTE")
    private Boolean computeEnabled;
    @SerializedName("LOCAL_COLLECTIONS")
    private Boolean localCollectionsEnabled;

    public FeaturesEntity() {
        super();
    }

    public Boolean isGeoFabricsEnabled() {
        return geoFabricsEnabled;
    }

    public Boolean isGraphsEnabled() {
        return graphsEnabled;
    }

    public Boolean isStreamsEnabled() {
        return streamsEnabled;
    }

    public Boolean isDynamoEnabled() {
        return dynamoEnabled;
    }

    public Boolean isSearchEnabled() {
        return searchEnabled;
    }

    public Boolean isCepEnabled() {
        return cepEnabled;
    }

    public Boolean isKvEnabled() {
        return kvEnabled;
    }

    public Boolean isDocsEnabled() {
        return docsEnabled;
    }

    public Boolean isUsersEnabled() {
        return usersEnabled;
    }

    public Boolean isComputeEnabled() {
        return computeEnabled;
    }

    public Boolean isLocalCollectionsEnabled() {
        return localCollectionsEnabled;
    }

}
