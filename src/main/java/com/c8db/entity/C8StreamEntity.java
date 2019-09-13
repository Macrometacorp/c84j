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

/**
 *
 */
public class C8StreamEntity implements Entity {

    private String name;
    /**
     * Special case where {@code _key} is used instead of {@code name}.
     */
    private String _key;
    private String db;
    @SerializedName("local")
    private Boolean isLocal;
    private String tenant;
    private String topic;
    private Integer type;

    public String getName() {
        return name != null ? name : _key;
    }

    public String getDb() {
        return db;
    }

    public Boolean getIsLocal() {
        return isLocal;
    }

    public String getTenant() {
        return tenant;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getType() {
        return type;
    }
}
