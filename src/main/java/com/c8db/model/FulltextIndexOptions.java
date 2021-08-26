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
public class FulltextIndexOptions extends IndexOptions {

    private Iterable<String> fields;
    private Integer minLength;

    public FulltextIndexOptions() {
        super(IndexType.fulltext);
    }

    protected Iterable<String> getFields() {
        return fields;
    }

    /**
     * @param fields A list of attribute paths
     * @return options
     */
    protected FulltextIndexOptions fields(final Iterable<String> fields) {
        this.fields = fields;
        return this;
    }

    public Integer getMinLength() {
        return minLength;
    }

    /**
     * @param minLength Minimum character length of words to index. Will default to
     *                  a server-defined value if unspecified. It is thus
     *                  recommended to set this value explicitly when creating the
     *                  index.
     * @return options
     */
    public FulltextIndexOptions minLength(final Integer minLength) {
        this.minLength = minLength;
        return this;
    }

}
