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
 * Modifications copyright (c) 2023 - 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.velocystream;

import java.util.HashMap;
import java.util.Map;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.annotations.Expose;
import com.c8db.credentials.C8Credentials;

/**
 *
 */
public class Request {

    private int version = 1;
    private int type = 1;
    private final String dbTenant;
    private final String pathTenant;
    private final String database;
    private final RequestType requestType;
    private final C8Credentials credentials;
    private final boolean retryEnabled;
    private final String request;
    private final Map<String, String> queryParam;
    private final Map<String, String> headerParam;
    @Expose(serialize = false)
    private RequestBody body;

    public Request(final String dbTenant, final String pathTenant, final String pathDatabase,
                   final RequestType requestType,
                   final String path) {
        this(dbTenant, pathTenant, pathDatabase, requestType, null, true, path);
    }

    public Request(final String dbTenant, final String pathTenant, final String pathDatabase,
                   final RequestType requestType,
                   final String path, C8Credentials credentials) {
        this(dbTenant, pathTenant, pathDatabase, requestType, credentials, true, path);
    }

    /**
     *
     * @param dbTenant - this attribute always not empty. It comes from c8db.db(<dbTenant>, ..)
     * @param pathTenant  - this attribute empty if request doesn't have `/_tenant/<pathTenant>` an a request.
     * @param database - - this attribute empty if request doesn't have `/_fabric/<fabric>` an a request.
     * @param requestType - HTTP type of request
     * @param retryEnabled - use retry strategy
     * @param path - path suffix for a request
     */
    public Request(final String dbTenant, final String pathTenant, final String database, final RequestType requestType,
                   final C8Credentials credentials, final boolean retryEnabled, final String path) {
        super();
        this.dbTenant = dbTenant;
        this.pathTenant = pathTenant;
        this.database = database;
        this.request = path;
        this.requestType = requestType;
        body = null;
        queryParam = new HashMap<String, String>();
        headerParam = new HashMap<String, String>();
        this.credentials = credentials;
        this.retryEnabled = retryEnabled;
    }

    public int getVersion() {
        return version;
    }

    public Request setVersion(final int version) {
        this.version = version;
        return this;
    }

    public int getType() {
        return type;
    }

    public Request setType(final int type) {
        this.type = type;
        return this;
    }

    public String getPathTenant() {
        return pathTenant;
    }

    public String getDbTenant() {
        return dbTenant;
    }

    public String getPathDatabase() {
        return database;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getRequest() {
        return request;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public Request putQueryParam(final String key, final Object value) {
        if (value != null) {
            queryParam.put(key, value.toString());
        }
        return this;
    }

    public Map<String, String> getHeaderParam() {
        return headerParam;
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public C8Credentials getCredentials() {
        return credentials;
    }

    public Request putHeaderParam(final String key, final String value) {
        if (value != null) {
            headerParam.put(key, value);
        }
        return this;
    }

    public RequestBody getBody() {
        return body;
    }

    public Request setBody(final VPackSlice body) {
        this.body = new JsonRequestBody(body);
        return this;
    }

    public Request setBody(final RequestBody body) {
        this.body = body;
        return this;
    }

}
