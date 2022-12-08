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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.exception.VPackParserException;
import com.c8db.C8DBException;
import com.c8db.internal.net.HostHandler;
import com.c8db.internal.velocystream.internal.AuthenticationRequest;
import com.c8db.internal.velocystream.internal.Message;
import com.c8db.internal.velocystream.internal.VstConnectionSync;
import com.c8db.util.C8Serialization;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

import java.util.Map;

/**
 *
 */
public class VstCommunicationSync extends VstCommunication<Response, VstConnectionSync> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VstCommunicationSync.class);

    public static class Builder {

        private final Map<Service, HostHandler> hostHandlerMatrix;
        private Integer timeout;
        private Long connectionTtl;
        private String user;
        private String password;
        private Boolean useSsl;
        private SSLContext sslContext;
        private Integer chunksize;
        private Integer maxConnections;

        public Builder(final Map<Service, HostHandler> hostHandlerMatrix) {
            super();
            this.hostHandlerMatrix = hostHandlerMatrix;
        }

        public Builder(final Builder builder) {
            this(builder.hostHandlerMatrix);
            timeout(builder.timeout).user(builder.user).password(builder.password).useSsl(builder.useSsl)
                    .sslContext(builder.sslContext).chunksize(builder.chunksize).maxConnections(builder.maxConnections);
        }

        public Builder timeout(final Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder user(final String user) {
            this.user = user;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
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

        public Builder chunksize(final Integer chunksize) {
            this.chunksize = chunksize;
            return this;
        }

        public Builder maxConnections(final Integer maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder connectionTtl(final Long connectionTtl) {
            this.connectionTtl = connectionTtl;
            return this;
        }

        public VstCommunication<Response, VstConnectionSync> build(final C8Serialization util) {
            return new VstCommunicationSync(hostHandlerMatrix, timeout, user, password, useSsl, sslContext, util, chunksize,
                    maxConnections, connectionTtl);
        }

    }

    protected VstCommunicationSync(final Map<Service, HostHandler> hostHandlerMatrix, final Integer timeout, final String user,
            final String password, final Boolean useSsl, final SSLContext sslContext, final C8Serialization util,
            final Integer chunksize, final Integer maxConnections, final Long ttl) {
        super(timeout, user, password, useSsl, sslContext, util, chunksize, hostHandlerMatrix);
    }

    @Override
    protected Response execute(final Request request, final VstConnectionSync connection) throws C8DBException {
        try {
            final Message requestMessage = createMessage(request);
            final Message responseMessage = send(requestMessage, connection);
            final Response response = createResponse(responseMessage);
            checkError(response);
            return response;
        } catch (final VPackParserException e) {
            throw new C8DBException(e);
        }
    }

    private Message send(final Message message, final VstConnectionSync connection) throws C8DBException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Send Message (id=%s, head=%s, body=%s)", message.getId(), message.getHead(),
                    message.getBody() != null ? message.getBody() : "{}"));
        }
        return connection.write(message, buildChunks(message));
    }

    @Override
    protected void authenticate(final VstConnectionSync connection) {
        final Response response = execute(
                new AuthenticationRequest(user, password != null ? password : "", ENCRYPTION_PLAIN), connection);
        checkError(response);
    }

}
