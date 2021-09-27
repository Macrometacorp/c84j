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

/**
 *
 */
public class UserQuery implements Entity {

    private UserQueryOptions query;
    // Macrometa Corp Modification: Add `user` field.
    private String user;

    public UserQuery() {
        super();
    }

    public UserQuery(UserQueryOptions query, String user) {
        super();
        this.query = query;
        this.user = user;
    }

    public UserQueryOptions getQuery() {
        return query;
    }

    public String getUser() {
        return user;
    }

}
