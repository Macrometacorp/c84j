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
 * Modifications copyright (c) 2022 Macrometa Corp All rights reserved.
 *
 */

package com.c8db.internal.http;

import com.c8db.SecretProvider;
import javax.net.ssl.SSLContext;

import com.c8db.Protocol;
import com.c8db.Service;
import com.c8db.credentials.C8Credentials;
import com.c8db.internal.net.Connection;
import com.c8db.internal.net.ConnectionFactory;
import com.c8db.internal.net.HostDescription;
import com.c8db.util.C8Serialization;

public class HttpConnectionFactory implements ConnectionFactory {

    private final HttpConnection.Builder builder;

    public HttpConnectionFactory(final C8Credentials credentials, final Integer timeout, final Integer responseSizeLimit,
                                 final Boolean useSsl, final SSLContext sslContext, final C8Serialization util,
                                 final Protocol protocol, final Long connectionTtl, String httpCookieSpec,
                                 final HostDescription auxiliaryHost, Integer retryTimeout) {
        super();
        builder = new HttpConnection.Builder().timeout(timeout).responseSizeLimit(responseSizeLimit)
                .credentials(credentials).useSsl(useSsl).sslContext(sslContext).serializationUtil(util)
                .contentType(protocol).ttl(connectionTtl).httpCookieSpec(httpCookieSpec).auxHost(auxiliaryHost)
                .retryTimeout(retryTimeout);
    }

    public HttpConnectionFactory(final C8Credentials credentials, final Integer timeout, final Integer responseSizeLimit,
                                 final SecretProvider secretProvider, final Boolean useSsl, final SSLContext sslContext,
                                 final C8Serialization util, final Protocol protocol, final Long connectionTtl,
                                 final String httpCookieSpec, final HostDescription auxiliaryHost, Integer retryTimeout) {
        super();
        builder = new HttpConnection.Builder().timeout(timeout).responseSizeLimit(responseSizeLimit)
                .secretProvider(secretProvider).credentials(credentials).useSsl(useSsl).sslContext(sslContext)
                .serializationUtil(util).contentType(protocol).ttl(connectionTtl).httpCookieSpec(httpCookieSpec)
                .auxHost(auxiliaryHost).retryTimeout(retryTimeout);
    }

    @Override
    public Connection create(final HostDescription host, final Service service) {
        return builder.host(host).service(service).build();
    }

}
