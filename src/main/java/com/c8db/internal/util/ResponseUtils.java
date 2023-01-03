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
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 *
 */

package com.c8db.internal.util;

import com.arangodb.velocypack.exception.VPackParserException;
import com.c8db.C8DBException;
import com.c8db.entity.ErrorEntity;
import com.c8db.internal.net.C8DBRedirectException;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Response;

/**
 *
 */
public final class ResponseUtils {

    private static final int ERROR_STATUS = 300;
    private static final int ERROR_INTERNAL = 503;
    private static final String HEADER_ENDPOINT = "X-C8-Endpoint";

    private ResponseUtils() {
        super();
    }

    public static void checkError(final C8Serialization util, final Response response) throws C8DBException {
        try {
            final int responseCode = response.getResponseCode();
            if (responseCode >= ERROR_STATUS) {
                if (responseCode == ERROR_INTERNAL && response.getMeta().containsKey(HEADER_ENDPOINT)) {
                    throw new C8DBRedirectException(String.format("Response Code: %s", responseCode),
                            response.getMeta().get(HEADER_ENDPOINT));
                } else if (responseCode == ERROR_INTERNAL) {
                    throw new C8DBException(String.format("Response Code: %s", responseCode), responseCode);
                }

                if (response.getBody() != null) {
                    final ErrorEntity errorEntity = util.deserialize(response.getBody(), ErrorEntity.class);
                    if (errorEntity.getException() != null || errorEntity.getErrorMessage() != null ||
                        errorEntity.getCode() != 0 || errorEntity.getErrorNum() != 0) {
                        // it means that response meets ErrorEntity class
                        throw new C8DBException(errorEntity);
                    } else {
                        throw new C8DBException(response.getBody().toString(), responseCode);
                    }
                } else {
                    throw new C8DBException(String.format("Response Code: %s", responseCode), responseCode);
                }
            }
        } catch (final VPackParserException e) {
            throw new C8DBException(e);
        }
    }
}
