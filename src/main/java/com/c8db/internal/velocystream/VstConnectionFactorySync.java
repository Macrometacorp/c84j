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

package com.c8db.internal.velocystream;

import javax.net.ssl.SSLContext;

import com.c8db.Service;
import com.c8db.internal.net.Connection;
import com.c8db.internal.net.ConnectionFactory;
import com.c8db.internal.net.HostDescription;
import com.c8db.internal.velocystream.internal.MessageStore;
import com.c8db.internal.velocystream.internal.VstConnectionSync;

/**
 *
 */
public class VstConnectionFactorySync implements ConnectionFactory {

    private final VstConnectionSync.Builder builder;

    public VstConnectionFactorySync(final Integer timeout, final Long connectionTtl,
            final Boolean useSsl, final SSLContext sslContext) {
        super();
        builder = new VstConnectionSync.Builder().timeout(timeout).ttl(connectionTtl).useSsl(useSsl)
                .sslContext(sslContext);
    }

    @Override
    public Connection create(final HostDescription host, final Service service) {
        return builder.messageStore(new MessageStore()).host(host).service(service).build();
    }

}
