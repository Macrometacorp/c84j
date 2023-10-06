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

package com.c8db.model;

import com.c8db.entity.CollectionType;
import com.c8db.entity.KeyOptions;
import com.c8db.entity.KeyType;

public class CollectionCreateBodyOptions {

    private String name;
    private KeyOptions keyOptions;
    private Boolean isSpot;
    private String[] shardKeys;
    private CollectionType type;
    private Boolean isLocal;
    private Boolean isSystem;
    private Boolean stream;
    private Boolean enableShards;
    private Boolean waitForSync;
    private Boolean strongConsistency;
    private Boolean cacheEnabled;

    public CollectionCreateBodyOptions() {}

    protected CollectionCreateBodyOptions name(final String name) {
        this.name = name;
        return this;
    }

    public CollectionCreateBodyOptions keyOptions(final KeyOptions keyOptions) {
        this.keyOptions = keyOptions;
        return this;
    }

    public CollectionCreateBodyOptions shardKeys(final String... shardKeys) {
        this.shardKeys = shardKeys;
        return this;
    }

    public CollectionCreateBodyOptions type(final CollectionType type) {
        this.type = type;
        return this;
    }

    public CollectionCreateBodyOptions isSpot(final Boolean isSpot) {
        this.isSpot = isSpot;
        return this;
    }

    public CollectionCreateBodyOptions isLocal(final Boolean isLocal) {
        this.isLocal = isLocal;
        return this;
    }

    public CollectionCreateBodyOptions stream(final Boolean stream) {
        this.stream = stream;
        return this;
    }

    public CollectionCreateBodyOptions isSystem(final Boolean isSystem) {
        this.isSystem = isSystem;
        return this;
    }

    public CollectionCreateBodyOptions enableShards(final Boolean enableShards) {
        this.enableShards = enableShards;
        return this;
    }

    public CollectionCreateBodyOptions waitForSync(final Boolean waitForSync) {
        this.waitForSync = waitForSync;
        return this;
    }

    public CollectionCreateBodyOptions strongConsistency(final Boolean strongConsistency) {
        this.strongConsistency = strongConsistency;
        return this;
    }

    public CollectionCreateBodyOptions cacheEnabled(final Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }
}
