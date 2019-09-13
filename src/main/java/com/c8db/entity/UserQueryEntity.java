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

import java.util.Map;

import com.arangodb.velocypack.annotations.SerializedName;

/**
 *
 */
public class UserQueryEntity implements Entity {

    @SerializedName("userid")
    private String userId;
    private String tenant;
    private String fabric;
    private String name;
    @SerializedName("parameter")
    private Map<String, Object> bindVars;
    @SerializedName("value")
    private String query;
    private String type;

    public UserQueryEntity() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public String getTenant() {
        return tenant;
    }

    public String getFabric() {
        return fabric;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getBindVars() {
        return bindVars;
    }

    public String getQuery() {
        return query;
    }

    public String getType() {
        return type;
    }
}
