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
 */

package com.c8db.internal.util;

import com.c8db.util.C8Serialization;

/**
 *
 */
public class C8SerializationFactory {

    public enum Serializer {
        INTERNAL, CUSTOM
    }

    private final C8Serialization interal;
    private final C8Serialization custom;

    public C8SerializationFactory(final C8Serialization interal, final C8Serialization custom) {
        super();
        this.interal = interal;
        this.custom = custom;
    }

    public C8Serialization get(final Serializer serializer) {
        switch (serializer) {
        case CUSTOM:
            return custom;
        case INTERNAL:
        default:
            return interal;
        }
    }

}
