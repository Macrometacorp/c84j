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

import com.c8db.entity.IndexType;

/**
 *
 */
public class PersistentIndexOptions extends IndexOptions {

    private Iterable<String> fields;
    private Boolean unique;
    private Boolean sparse;
    private Boolean deduplicate;

    public PersistentIndexOptions() {
        super(IndexType.persistent);
    }

    protected Iterable<String> getFields() {
        return fields;
    }

    /**
     * @param fields A list of attribute paths
     * @return options
     */
    protected PersistentIndexOptions fields(final Iterable<String> fields) {
        this.fields = fields;
        return this;
    }

    public Boolean getUnique() {
        return unique;
    }

    /**
     * @param unique if true, then create a unique index
     * @return options
     */
    public PersistentIndexOptions unique(final Boolean unique) {
        this.unique = unique;
        return this;
    }

    public Boolean getSparse() {
        return sparse;
    }

    /**
     * @param sparse if true, then create a sparse index
     * @return options
     */
    public PersistentIndexOptions sparse(final Boolean sparse) {
        this.sparse = sparse;
        return this;
    }

    public Boolean getDeduplicate() {
        return deduplicate;
    }

    /**
     * @param deduplicate if false, the deduplication of array values is turned off.
     * @return options
     */
    public PersistentIndexOptions deduplicate(final Boolean deduplicate) {
        this.deduplicate = deduplicate;
        return this;
    }

}
