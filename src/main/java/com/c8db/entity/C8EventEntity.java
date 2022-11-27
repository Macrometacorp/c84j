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
import lombok.Data;

import java.util.Map;

/**
 *
 */
@Data
public class C8EventEntity implements Entity {

    @SerializedName("_id")
    private String id;
    @SerializedName("_key")
    private String key;
    @SerializedName("_rev")
    private String revision;
    private String status;
    private String description;
    private String clusterID;
    private String email;
    private String entityName;
    private String entityType;
    private String fabric;
    private String tenant;
    private String user;
    private String details;
    private String action;
    private Map<String, Object> attributes;

}
