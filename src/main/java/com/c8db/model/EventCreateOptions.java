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

package com.c8db.model;

import java.util.Map;

/**
 * 
 */
public class EventCreateOptions {
    private String status;
    private String description;
    private String email;
    private String entityName;
    private String entityType;
    private String fabric;
    private String tenant;
    private String user;
    private String details;
    private String action;
    private Map<String, String> attributes;

    public EventCreateOptions() {
        super();
    }

    public String getDescription() {
        return description;
    }

    public EventCreateOptions description(String description) {
        this.description = description;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public EventCreateOptions email(String email) {
        this.email = email;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public EventCreateOptions entityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public EventCreateOptions entityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getFabric() {
        return fabric;
    }

    public EventCreateOptions fabric(String fabric) {
        this.fabric = fabric;
        return this;
    }

    public String getTenant() {
        return tenant;
    }

    public EventCreateOptions tenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public String getUser() {
        return user;
    }

    public EventCreateOptions user(String user) {
        this.user = user;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public EventCreateOptions details(String details) {
        this.details = details;
        return this;
    }

    public String getAction() {
        return action;
    }

    public EventCreateOptions action(String action) {
        this.action = action;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public EventCreateOptions status(String status) {
        this.status = status;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public EventCreateOptions attributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }
}
