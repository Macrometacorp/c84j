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

package com.c8db;

import com.c8db.entity.StreamAccessLevel;

/**
 * Interface for operations on users level.
 *
 */
public interface C8User extends C8SerializationAccessor {

    /**
     * Get the stream access level
     * @param user user name
     * @param stream stream name
     * @return result of access level. Possible results are `ro`, `rw`, `none`
     */
    StreamAccessLevel getStreamAccessLevel(final String user, final String stream);

    // TODO: Implement other required user features.
}
