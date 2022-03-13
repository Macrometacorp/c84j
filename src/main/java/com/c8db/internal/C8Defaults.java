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

package com.c8db.internal;

import com.c8db.Protocol;
import com.c8db.entity.LoadBalancingStrategy;

/**
 *
 */
public final class C8Defaults {

    private C8Defaults() {
        super();
    }

    public static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

	public static final String DEFAULT_HOST = "127.0.0.1";
//	public static final Integer DEFAULT_PORT = 8529;
    public static final Integer DEFAULT_PORT = 443;
    public static final Integer DEFAULT_STREAM_ADMIN_PORT = 8080;
    //TODO: update this
    public static final String DEFAULT_DC_LIST = "tonchev-europe-west4,tonchev-europe-west1";
    public static final String DEFAULT_TENANT = "demo";
    public static final Integer DEFAULT_TIMEOUT = 0;
    public static final String DEFAULT_USER = "root";
    public static final Boolean DEFAULT_USE_SSL = true;
    public static final Boolean DEFAULT_JWT_AUTH = true;
    public static final int CHUNK_MIN_HEADER_SIZE = INTEGER_BYTES + INTEGER_BYTES + LONG_BYTES;
    public static final int CHUNK_MAX_HEADER_SIZE = CHUNK_MIN_HEADER_SIZE + LONG_BYTES;
    public static final int CHUNK_DEFAULT_CONTENT_SIZE = 30000;
    public static final int MAX_CONNECTIONS_VST_DEFAULT = 1;
    public static final Integer CONNECTION_TTL_VST_DEFAULT = null;
    public static final int MAX_CONNECTIONS_HTTP_DEFAULT = 20;
    public static final Protocol DEFAULT_NETWORK_PROTOCOL = Protocol.HTTP_JSON;
    public static final boolean DEFAULT_ACQUIRE_HOST_LIST = false;
    public static final int DEFAULT_ACQUIRE_HOST_LIST_INTERVAL = 60 * 60 * 1000; // hour
    public static final LoadBalancingStrategy DEFAULT_LOAD_BALANCING_STRATEGY = LoadBalancingStrategy.NONE;

}
