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
 * Modifications copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;

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

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the rev
     */
    public String getRev() {
        return rev;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the local
     */
    public Boolean getLocal() {
        return local;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the spotRegion
     */
    public Boolean getSpotRegion() {
        return spotRegion;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the tags
     */
    public Tag getTags() {
        return tags;
    }

    /**
     * @return the locationInfo
     */
    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public static class Tag {

        private String api;
        private String url;

        /**
         * @return the role
         */
        public String getApi() {
            return api;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }
    }

    public static class LocationInfo {
        @SerializedName("_id")
        private String id;
        @SerializedName("_key")
        private String key;
        @SerializedName("_rev")
        private String rev;
        private String city;
        @SerializedName("countrycode")
        private String countryCode;
        @SerializedName("countryname")
        private String countryName;
        private Double latitude;
        private Double longitude;
        private String name;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @return the rev
         */
        public String getRev() {
            return rev;
        }

        /**
         * @return the city
         */
        public String getCity() {
            return city;
        }

        /**
         * @return the countryCode
         */
        public String getCountryCode() {
            return countryCode;
        }

        /**
         * @return the countryName
         */
        public String getCountryName() {
            return countryName;
        }

        /**
         * @return the latitude
         */
        public Double getLatitude() {
            return latitude;
        }

        /**
         * @return the longitude
         */
        public Double getLongitude() {
            return longitude;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
    }
}
