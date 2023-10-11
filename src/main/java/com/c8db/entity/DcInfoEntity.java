/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
public class DcInfoEntity implements Entity {

    @SerializedName("_id")
    private String id;
    @SerializedName("_key")
    private String key;
    @SerializedName("_rev")
    private String rev;
    private String host;
    private Boolean local;
    private String name;
    private int port;
    @SerializedName("spot_region")
    private Boolean spotRegion;
    private int status;
    private Tag tags;
    private LocationInfo locationInfo;

    @Getter
    public static class Tag {
        private String api;
        private String url;
    }

    @AllArgsConstructor
    @Getter
    public static class LocationInfo {
        private String city;
        @SerializedName("countrycode")
        private String countryCode;
        @SerializedName("countryname")
        private String countryName;
        private Double latitude;
        private Double longitude;
        private String url;
    }
}
