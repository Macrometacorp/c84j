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

package com.c8db.internal.velocystream.internal;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import com.c8db.C8DBException;
import com.c8db.Service;
import com.c8db.internal.net.HostDescription;

/**
 *
 */
public class VstConnectionSync extends VstConnection {

    public static class Builder {

        private HostDescription host;
        private MessageStore messageStore;
        private Integer timeout;
        private Long ttl;
        private Boolean useSsl;
        private SSLContext sslContext;
        private Service service;

        public Builder host(final HostDescription host) {
            this.host = host;
            return this;
        }

        public Builder messageStore(final MessageStore messageStore) {
            this.messageStore = messageStore;
            return this;
        }

        public Builder timeout(final Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder useSsl(final Boolean useSsl) {
            this.useSsl = useSsl;
            return this;
        }

        public Builder sslContext(final SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Builder ttl(final Long ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder service(final Service service) {
            this.service = service;
            return this;
        }

        public VstConnectionSync build() {
            return new VstConnectionSync(host, timeout, ttl, useSsl, sslContext, messageStore, service);
        }
    }

    private VstConnectionSync(final HostDescription host, final Integer timeout, final Long ttl, final Boolean useSsl,
            final SSLContext sslContext, final MessageStore messageStore, final Service service) {
        super(host, timeout, ttl, useSsl, sslContext, messageStore, service);
    }

    public Message write(final Message message, final Collection<Chunk> chunks) throws C8DBException {
        final FutureTask<Message> task = new FutureTask<Message>(new Callable<Message>() {
            @Override
            public Message call() throws Exception {
                return messageStore.get(message.getId());
            }
        });
        messageStore.storeMessage(message.getId(), task);
        super.writeIntern(message, chunks);
        try {
            return timeout == null || timeout == 0L ? task.get() : task.get(timeout, TimeUnit.MILLISECONDS);
        } catch (final Exception e) {
            throw new C8DBException(e);
        }
    }

}
