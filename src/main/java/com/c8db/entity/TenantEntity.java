/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TenantEntity {

    private String tenant;
    private String dcList;
    private String status;
    @SerializedName("associated_regions")
    private List<String> associatedRegions;
    private DnsInfo dnsInfo;

    @Data
    public static class DnsInfo {

        @SerializedName("regional_urls")
        private Map<String, String> regionalUrls;
        private Map<String, String> status;
        @SerializedName("err_msg")
        private String errMsg;
        @SerializedName("global_url")
        private String globalUrl;
    }
}
