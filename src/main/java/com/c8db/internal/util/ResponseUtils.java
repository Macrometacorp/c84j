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

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackParserException;
import com.c8db.C8DBException;
import com.c8db.Protocol;
import com.c8db.entity.CursorEntity;
import com.c8db.entity.ErrorEntity;
import com.c8db.internal.net.C8DBRedirectException;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer.Options;
import com.c8db.velocystream.MultipartRequest;
import com.c8db.velocystream.MultipartResponse;
import com.c8db.velocystream.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

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

    public static Response buildResponse(final C8Serialization util, final CloseableHttpResponse httpResponse,
        final Protocol contentType) throws UnsupportedOperationException, IOException {
        Response response = new Response();
        response.setResponseCode(httpResponse.getStatusLine().getStatusCode());
        final HttpEntity entity = httpResponse.getEntity();

        if(null != entity.getContentType()
                && MultipartRequest.BATCH_CONTENT_TYPE.equals(entity.getContentType().getValue())){

            MultipartResponse mutipartResponse = new MultipartResponse(util);

            mutipartResponse.parseMultipartResponse(entity.getContent());



            /*
                    Mutipart response
                    ---------------------------
                   1. create class MutipartResponse
                   2. MutipartResponse should extend Response object
                   3. multipart object should have list of HttpResponseEntity
                   4. From each  HttpResponseEntity, create CursorEntity
                   5. i.e. create a list of CursorEntity from MutipartResponse
                   6. create List<C8Cursor<T>> from CursorEntity
             */



        } else if (entity != null && entity.getContent() != null) {
            if (contentType == Protocol.HTTP_VPACK) {
                final byte[] content = IOUtils.toByteArray(entity.getContent());
                if (content.length > 0) {
                    response.setBody(new VPackSlice(content));
                }
            } else {
                final String content = IOUtils.toString(entity.getContent());
                if (!content.isEmpty()) {
                    try {
                        response.setBody(
                            util.serialize(content, new Options().stringAsJson(true).serializeNullValues(true)));
                    } catch (C8DBException e) {
                        final byte[] contentAsByteArray = content.getBytes();
                        if (contentAsByteArray.length > 0) {
                            response.setBody(new VPackSlice(contentAsByteArray));
                        }
                    }
                }
            }
        }
        final Header[] headers = httpResponse.getAllHeaders();
        final Map<String, String> meta = response.getMeta();
        for (final Header header : headers) {
            meta.put(header.getName(), header.getValue());
        }
        return response;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

//    private static List<CursorEntity> parseMultipartResponse(String multipartResponse) {
//        List<CursorEntity> resultList = new ArrayList<>();
//        String[] parts = multipartResponse.split("--C8_BATCH_QUERY_PART\r\n");
//        for (String part : parts) {
//            if (!part.trim().isEmpty()) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                String jsonStr = part.substring(part.indexOf("{"), part.lastIndexOf("}") + 1);
//                try {
//                    //CursorEntity cursor = objectMapper.readValue(jsonStr, CursorEntity.class);
//                    JsonParser parser = new JsonParser();
//                    JsonElement element = parser.parse(json);
//                    JsonObject obj = element.getAsJsonObject();
//                    JsonElement result = obj.get("result");
//
//                    // Convert result to VPackSlice
//                    VPack vpack = new VPack();
//                    VPackParser vpackParser = new VPackParser(vpack);
//                    VPackSlice slice = vpackParser.fromJson(result.toString());
//
//                   // resultList.add(cursor);
//                } catch (Exception e) {
//                    System.err.println("Error parsing JSON: " + e.getMessage());
//                }
//            }
//        }
//        return resultList;
//    }
}
