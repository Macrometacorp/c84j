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
