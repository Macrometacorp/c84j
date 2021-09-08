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

import java.util.Map;

/**
 * 
 * @see <a href=
 *      "https://docs.arangodb.com/current/HTTP/Gharial/Management.html#create-a-graph">API
 *      Documentation</a>
 */
public class UserQueryOptions {

    private String name;
    private Map<String, Object> parameter;
    private String value;
    // Macrometa Corp Modification: Add `user` field.
    private String user;

    public String getName() {
        return name;
    }

    public UserQueryOptions name(final String name) {
        this.name = name;
        return this;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public UserQueryOptions parameter(final Map<String, Object> parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getValue() {
        return value;
    }

    public UserQueryOptions value(final String value) {
        this.value = value;
        return this;
    }

    public String getUser() {
        return user;
    }

    public UserQueryOptions user(final String user) {
        this.user = user;
        return this;
    }

}
