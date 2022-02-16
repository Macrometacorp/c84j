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

package com.c8db.internal.http;

import javax.net.ssl.SSLContext;

import com.c8db.Protocol;
import com.c8db.internal.net.Connection;
import com.c8db.internal.net.ConnectionFactory;
import com.c8db.internal.net.HostDescription;
import com.c8db.util.C8Serialization;

/**
 *
 */
public class HttpConnectionFactory implements ConnectionFactory {

    private final HttpConnection.Builder builder;

    public HttpConnectionFactory(final Integer timeout, final String user, final String password, final String email,
            final Boolean jwtAuth, final Boolean useSsl, final SSLContext sslContext, final C8Serialization util,
            final Protocol protocol, final Long connectionTtl, String httpCookieSpec,String jwtToken,String apiKey) {
        super();
        builder = new HttpConnection.Builder().timeout(timeout).user(user).password(password).email(email)
                .jwtAuthEnabled(jwtAuth).useSsl(useSsl).sslContext(sslContext).serializationUtil(util)
                .contentType(protocol).ttl(connectionTtl).httpCookieSpec(httpCookieSpec).jwt(jwtToken)
                .apiKey(apiKey);
    }

    @Override
    public Connection create(final HostDescription host) {
        return builder.host(host).build();
    }

}
