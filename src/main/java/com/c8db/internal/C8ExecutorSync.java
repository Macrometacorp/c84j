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

package com.c8db.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.c8db.Service;
import com.c8db.entity.CursorEntities;
import com.c8db.entity.CursorEntity;
import com.c8db.entity.Entity;
import com.c8db.entity.ErrorEntity;
import com.c8db.velocystream.MultipartRequest;
import com.c8db.velocystream.MultipartResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.entity.MetaAware;
import com.c8db.internal.net.CommunicationProtocol;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.Response;

/**
 *
 */
public class C8ExecutorSync extends C8Executor {

    private static final Logger LOG = LoggerFactory.getLogger(C8ExecutorSync.class);

    private final CommunicationProtocol protocol;

    public C8ExecutorSync(final CommunicationProtocol protocol, final C8SerializationFactory util,
            final DocumentCache documentCache) {
        super(util, documentCache);
        this.protocol = protocol;
    }

    public <T> T execute(final Request request, final Type type) throws C8DBException {
        return execute(request, type, null);
    }

    public <T> T execute(final Request request, final Type type, final HostHandle hostHandle) throws C8DBException {
        return execute(request, type, hostHandle, Service.C8DB);
    }

    public <T> CursorEntities<T> execute(final MultipartRequest request, final Type type, final HostHandle hostHandle) throws C8DBException {
        return execute(request, new ResponseDeserializer<CursorEntities> () {
            final List<CursorEntity<T>> entities = new ArrayList<>();
            @Override
            public T deserialize(final Response response) throws VPackException {
                MultipartResponse multipartResponse = (MultipartResponse) response;
                List<Response> responseList = multipartResponse.geResponseList();
                for(Response resp : responseList){
                    entities.add(createResult(type, resp));
                }
                return new CursorEntities<>(entities);
            }
        }, hostHandle);
    }


    public <T> T execute(final Request request, final Type type, final HostHandle hostHandle, Service service) throws C8DBException {
        return execute(request, new ResponseDeserializer<T>() {
            @Override
            public T deserialize(final Response response) throws VPackException {
                T result = createResult(type, response);
                return result;
            }
        }, hostHandle, service);
    }

    public <T> T execute(final Request request, final ResponseDeserializer<T> responseDeserializer)
            throws C8DBException {
        return execute(request, responseDeserializer, null);
    }

    public <T> T execute(final Request request, final ResponseDeserializer<T> responseDeserializer,
                         final HostHandle hostHandle) throws C8DBException {
        return execute(request, responseDeserializer, hostHandle, Service.C8DB);
    }

    public <T> T execute(final Request request, final ResponseDeserializer<T> responseDeserializer,
            final HostHandle hostHandle, Service service) throws C8DBException {

        try {

            final Response response = protocol.execute(request, hostHandle, service);
            T deserialize = responseDeserializer.deserialize(response);

            if (deserialize instanceof MetaAware) {
                LOG.debug("Respone is MetaAware " + deserialize.getClass().getName());
                ((MetaAware) deserialize).setMeta(response.getMeta());
            }

            return deserialize;

        } catch (final VPackException e) {
            throw new C8DBException(e);
        }
    }

    public void disconnect() {
        try {
            protocol.close();
        } catch (final IOException e) {
            throw new C8DBException(e);
        }
    }
}
