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

package com.c8db.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Index type enum.
 */
// Macrometa Corp Modification: Improve IndexType enum.
public enum IndexType {
    primary("primary"),
    hash("hash"),
    skiplist("skiplist"),
    persistent("persistent"),
    geo("geo"),
    geo1("geo1"),
    geo2("geo2"),
    fulltext("fulltext"),
    edge("edge"),
    ttl("ttl");

    private final String value;
    private static final Map<String, IndexType> lookup = new HashMap<>();

    static {
        for (IndexType type : EnumSet.allOf(IndexType.class)) {
            lookup.put(type.getValue().toLowerCase(), type);
        }
    }

    IndexType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static IndexType fromValue(final String value) {
        if (value != null) {
            return lookup.get(value.toLowerCase());
        }
        return null;
    }
}
