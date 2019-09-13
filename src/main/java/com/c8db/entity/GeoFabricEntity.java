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

public class GeoFabricEntity implements Entity {

    private String name;
    private GeoFabricOptions options;
    private Boolean isSystem;

    /**
     * @return the name of the database/geofabric
     */
    public String getName() {
        return name;
    }

    /**
     * @return the options of the database/geofabric
     */
    public GeoFabricOptions getOptions() {
        return options;
    }

    /**
     * @return whether or not the database is the _system database
     */
    public Boolean getIsSystem() {
        return isSystem;
    }

    public static class GeoFabricOptions {
        private String dcList;
        private Boolean global;
        private Boolean isSystem;
        private String name;
        private String origin;
        private Boolean realTime;
        private String spotDc;
        private String status;
        private String tenant;

        /**
         * @return the dcList
         */
        public String getDcList() {
            return dcList;
        }

        /**
         * @return the global
         */
        public Boolean getGlobal() {
            return global;
        }

        /**
         * @return the isSystem
         */
        public Boolean getIsSystem() {
            return isSystem;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the origin
         */
        public String getOrigin() {
            return origin;
        }

        /**
         * @return the realTime
         */
        public Boolean getRealTime() {
            return realTime;
        }

        /**
         * @return the spotDc
         */
        public String getSpotDc() {
            return spotDc;
        }

        /**
         * @return the status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @return the tenant
         */
        public String getTenant() {
            return tenant;
        }
    }
}
