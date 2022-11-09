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

import java.util.Collection;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.C8EventEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.DocumentCreateOptions;
import com.c8db.model.DocumentDeleteOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.EventCreateOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 */
public abstract class InternalC8Event<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_EVENT = "/_api/events";

    private static final String RETURN_OLD = "returnOld";
    private static final String SILENT = "silent";

    private final D db;

    protected InternalC8Event(final D db) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    public D db() {
        return db;
    }

    protected <T> Request insertEventRequest(final T value, final EventCreateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_EVENT);
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected <T> ResponseDeserializer<C8EventEntity> insertEventResponseDeserializer(final T value,
            final EventCreateOptions options) {
        return new ResponseDeserializer<C8EventEntity>() {
            @Override
            public C8EventEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), C8EventEntity.class);
            }
        };
    }

    protected Request getEventRequest(final String key, final DocumentReadOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_EVENT, key);

        final DocumentReadOptions params = (options != null ? options : new DocumentReadOptions());
        request.putHeaderParam(C8RequestParam.IF_NONE_MATCH, params.getIfNoneMatch());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        return request;
    }

    protected Request getEventsRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_EVENT);
    }

    protected ResponseDeserializer<Collection<C8EventEntity>> getEventsResponseDeserializer() {
        return new ResponseDeserializer<Collection<C8EventEntity>>() {
            @Override
            public Collection<C8EventEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody(), new Type<Collection<C8EventEntity>>() {
                }.getType());
            }
        };
    }

    protected Request deleteEventRequest(final String key, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_EVENT, key);
        final DocumentDeleteOptions params = (options != null ? options : new DocumentDeleteOptions());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        return request;
    }

    protected <T> Request deleteEventsRequest(final Collection<T> keys, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_EVENT);
        final DocumentDeleteOptions params = (options != null ? options : new DocumentDeleteOptions());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util().serialize(keys));
        return request;
    }

}
