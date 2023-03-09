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

package com.c8db.internal.util;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.Protocol;
import com.c8db.internal.http.HttpDeleteWithBody;
import com.c8db.internal.net.AccessType;
import com.c8db.internal.net.HostDescription;
import com.c8db.velocystream.MultipartRequest;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import static com.c8db.velocystream.MultipartRequest.BATCH_CONTENT_TYPE;

/**
 *
 */
public final class RequestUtils {

    private static final ContentType CONTENT_TYPE_APPLICATION_JSON_UTF8 = ContentType.APPLICATION_JSON;
    private static final ContentType CONTENT_TYPE_VPACK = ContentType.create("application/x-velocypack");

    private RequestUtils() {
        super();
    }

    public static AccessType determineAccessType(final Request request) {
        if (request.getRequestType() == RequestType.GET) {
            return AccessType.READ;
        }
        return AccessType.WRITE;
    }

    public static String buildBaseUrl(final HostDescription host, boolean useSsl) {
        return (Boolean.TRUE == useSsl ? "https://" : "http://") + host.getHost() + ":" + host.getPort();
    }

    public static HttpRequestBase buildHttpRequestBase(final Request request, final String url, Protocol contentType) {
        final HttpRequestBase httpRequest;
        switch (request.getRequestType()) {
            case POST:
                httpRequest = requestWithBody(new HttpPost(url), request, contentType);
                break;
            case PUT:
                httpRequest = requestWithBody(new HttpPut(url), request, contentType);
                break;
            case PATCH:
                httpRequest = requestWithBody(new HttpPatch(url), request, contentType);
                break;
            case DELETE:
                httpRequest = requestWithBody(new HttpDeleteWithBody(url), request, contentType);
                break;
            case HEAD:
                httpRequest = new HttpHead(url);
                break;
            case GET:
            default:
                httpRequest = new HttpGet(url);
                break;
        }
        return httpRequest;
    }

    private static HttpRequestBase requestWithBody(final HttpEntityEnclosingRequestBase httpRequest,
        final Request request, final Protocol contentType) {

        if(request instanceof MultipartRequest){
            MultipartRequest multipartRequest = (MultipartRequest)request;
            BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
            basicHttpEntity.setContentType(BATCH_CONTENT_TYPE);
            basicHttpEntity.setContent(new ByteArrayInputStream(multipartRequest.buildRequestBody().getBytes()));
            httpRequest.setEntity(basicHttpEntity);
            return httpRequest;
        }

        final VPackSlice body = request.getBody();
        if (body != null) {
            if (contentType == Protocol.HTTP_VPACK) {
                httpRequest.setEntity(new ByteArrayEntity(
                    Arrays.copyOfRange(body.getBuffer(), body.getStart(), body.getStart() + body.getByteSize()),
                    CONTENT_TYPE_VPACK));
            } else {
                httpRequest.setEntity(new StringEntity(body.toString(), CONTENT_TYPE_APPLICATION_JSON_UTF8));
            }
        }
        return httpRequest;
    }
}
