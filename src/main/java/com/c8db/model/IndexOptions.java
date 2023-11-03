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
 * Modifications copyright (c) 2021 Macrometa Corp All rights reserved.
 *
 */

package com.c8db.model;

import com.c8db.C8DBException;
import com.c8db.entity.IndexType;

/**
 * This class is used for all index similarities
 */
public class IndexOptions<R> extends MixinBase implements StrongConsistencyMixin<R> {

    private final IndexType type;
    private Boolean inBackground;
    // Macrometa Corp Modification: Add `name` field.
    private String name;

    public IndexOptions(IndexType type) {
        super();
        this.type = type;
    }

    /**
     * @param inBackground create the the index in the background this is a RocksDB
     *                     only flag.
     * @return options
     */
    public R inBackground(final Boolean inBackground) {
        this.inBackground = inBackground;
        return (R) this;
    }

    public Boolean getInBackground() {
        return inBackground;
    }

    // Macrometa Corp Modification: Introduce getType() abstraction.
    public IndexType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public R name(final String name) {
        this.name = name;
        return (R) this;
    }

    // Macrometa Corp Modification: Introduce create() method.
    public static IndexOptions create(IndexType indexType) {
        switch (indexType) {
            case hash:
                return new HashIndexOptions();
            case skiplist:
                return new SkiplistIndexOptions();
            case persistent:
                return new PersistentIndexOptions();
            case geo:
                return new GeoIndexOptions();
            case fulltext:
                return new FulltextIndexOptions();
            case ttl:
                return new TTLIndexOptions();
            case primary:
            case geo1:
            case geo2:
            case edge:
            default:
                throw new C8DBException(String.format("Creating index options for index type %s not supported.",
                        indexType.getValue()));
        }
    }

}
