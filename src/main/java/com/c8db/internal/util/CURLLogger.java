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

package com.c8db.internal.util;

import java.util.Map.Entry;

import com.c8db.velocystream.JsonRequestBody;
import org.apache.http.auth.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

/**
 *
 */
public final class CURLLogger {

    private static Logger LOGGER = LoggerFactory.getLogger(CURLLogger.class);

    private CURLLogger() {
    }

    public static void log(final String url, final Request request, final Credentials credencials,
            final C8Serialization util) {
        final RequestType requestType = request.getRequestType();
        final boolean includeBody = (requestType == RequestType.POST || requestType == RequestType.PUT
                || requestType == RequestType.PATCH || requestType == RequestType.DELETE) && request.getBody() != null;
        final StringBuilder buffer = new StringBuilder();
        if (includeBody) {
            buffer.append("\n");
            buffer.append("cat <<-___EOB___ | ");
        }
        buffer.append("curl -X ").append(requestType);
        buffer.append(" --dump -");
        if (request.getHeaderParam().size() > 0) {
            for (final Entry<String, String> header : request.getHeaderParam().entrySet()) {
                buffer.append(" -H '").append(header.getKey()).append(":").append(header.getValue()).append("'");
            }
        }
        if (credencials != null) {
            buffer.append(" -u ").append(credencials.getUserPrincipal().getName()).append(":")
                    .append(credencials.getPassword());
        }
        if (includeBody) {
            buffer.append(" -d @-");
        }
        buffer.append(" '").append(url).append("'");
        if (includeBody) {
            buffer.append("\n");
            buffer.append((String) util.deserialize(((JsonRequestBody)request.getBody()).getValue(), String.class));
            buffer.append("\n");
            buffer.append("___EOB___");
        }
        LOGGER.debug("[CURL] {}", buffer);
    }
}
