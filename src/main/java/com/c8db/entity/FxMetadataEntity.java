/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

/**
 * Class describes metadata of function worker.
 */
public class FxMetadataEntity implements Entity {

    private String accessToken;
    private String baseUri;
    private String clientSecret;
    private String clientToken;
    private String contractId;
    private String gdnApiKey;
    private String gdnApiKeyName;
    private String groupId;
    private String hostName;
    private String propertyId;
    private String resourceTierId;
    private FxType type;

    public String getAccessToken() {
        return accessToken;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientToken() {
        return clientToken;
    }

    public String getContractId() {
        return contractId;
    }

    public String getGdnApiKey() {
        return gdnApiKey;
    }

    public String getGdnApiKeyName() {
        return gdnApiKeyName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getResourceTierId() {
        return resourceTierId;
    }

    public FxType getType() {
        return type;
    }
}
