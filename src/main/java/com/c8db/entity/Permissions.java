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

/**
 *
 */
public enum Permissions {

    /**
     * read and write access
     */
    RW(0),
    /**
     * read-only access
     */
    RO(1),

    /*
     * no access
     */
    NONE(2),

    /**
     * default access
     */
    UNDEFINED(3);

    private final int level;

    private Permissions(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
