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
 * Modifications copyright (c) 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import com.c8db.C8DBException;
import com.c8db.credentials.C8Credentials;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.util.EncodeUtils;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

/**
 *
 */
public abstract class C8Executeable<E extends C8Executor> {

    private static final String SLASH = "/";

    protected final E executor;
    protected final C8SerializationFactory util;
    protected final C8Context context;
    protected final String dbTenant;
    private final C8Credentials credentials;

    protected C8Executeable(final E executor, final C8SerializationFactory util, final C8Context context,
                            final String dbTenant, final C8Credentials credentials) {
        super();
        this.executor = executor;
        this.util = util;
        this.context = context;
        this.dbTenant = dbTenant;
        this.credentials = credentials;
    }

    protected E executor() {
        return executor;
    }

    public C8Serialization util() {
        return util.get(Serializer.INTERNAL);
    }

    public C8Serialization util(final Serializer serializer) {
        return util.get(serializer);
    }

    public C8Credentials credentials() {
        return credentials;
    }

    protected Request request(final String pathTenant, final String pathDatabase, final RequestType requestType,
                              final String... path) {
        return request(pathTenant, pathDatabase, requestType, true, path);
    }

    protected Request request(final String pathTenant, final String pathDatabase, final RequestType requestType,
            final boolean retryEnabled, final String... path) {
        final Request request = new Request(dbTenant, pathTenant, pathDatabase, requestType, credentials, retryEnabled,
                createPath(path));
        for (final Entry<String, String> header : context.getHeaderParam().entrySet()) {
            request.putHeaderParam(header.getKey(), header.getValue());
        }
        return request;
    }

    protected static String createPath(final String... params) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                sb.append(SLASH);
            }
            try {
                final String param;
                if (params[i].contains(SLASH)) {
                    param = createPath(params[i].split(SLASH));
                } else {
                    param = EncodeUtils.encodeURL(params[i]);
                }
                sb.append(param);
            } catch (final UnsupportedEncodingException e) {
                throw new C8DBException(e);
            }
        }
        return sb.toString();
    }

}
