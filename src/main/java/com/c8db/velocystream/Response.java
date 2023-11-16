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
 * Modifications copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.velocystream;

import java.util.HashMap;
import java.util.Map;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.annotations.Expose;

/**
 *
 */
public class Response {

    private int version = 1;
    private int type = 2;
    private int responseCode;
    private Map<String, String> meta;
    @Expose(deserialize = false)
    private ResponseBody body = null;

    public Response() {
        super();
        meta = new HashMap<String, String>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(final int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(final Map<String, String> meta) {
        this.meta = meta;
    }

    public VPackSlice getBody() {
        if (body instanceof JsonResponseBody){
            return ((JsonResponseBody) body).getValue();
        }
        return null;
    }

    public MultipartResponseBody getMultipartBody() {
        if (body instanceof MultipartResponseBody) {
            return (MultipartResponseBody) body;
        }
        return null;
    }

    public void setBody(final VPackSlice body) {
        this.body = new JsonResponseBody(body);
    }

    public void setBody(final ResponseBody body) {
        this.body = body;
    }

}
