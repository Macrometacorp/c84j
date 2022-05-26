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
import java.util.Collection;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.entity.C8StreamBacklogEntity;
import com.c8db.entity.C8StreamStatisticsEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public abstract class InternalC8Stream<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_STREAMS = "/_api/streams";

    private final D db;
    private final String name;

    protected InternalC8Stream(final D db, final String name) {
        super(db.executor, db.util, db.context);
        this.db = db;
        this.name = name;
    }

    public D db() {
        return db;
    }

    public String name() {
        return name;
    }

    protected Request getC8StreamBacklogRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name, "backlog");
        return request;
    }

    protected ResponseDeserializer<C8StreamBacklogEntity> getC8StreamBacklogResponseDeserializer() {
        return new ResponseDeserializer<C8StreamBacklogEntity>() {
            @Override
            public C8StreamBacklogEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<C8StreamBacklogEntity>() {
                }.getType());
            }
        };
    }

    protected Request getC8StreamStatisticsRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name, "stats");
        return request;
    }

    protected ResponseDeserializer<C8StreamStatisticsEntity> getC8StreamStatisticsResponseDeserializer() {
        return new ResponseDeserializer<C8StreamStatisticsEntity>() {
            @Override
            public C8StreamStatisticsEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<C8StreamStatisticsEntity>() {
                }.getType());
            }
        };
    }

    protected Request deleteC8StreamRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_STREAMS, name);
        return request;
    }

    protected Request getC8StreamSubscriptionsRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name,
                "subscriptions");
        return request;
    }

    protected ResponseDeserializer<Collection<String>> getC8StreamSubscriptionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Collection<String>>() {
                }.getType());
            }
        };
    }

    protected Request expireMessagesRequest(int expireTimeInSeconds) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "expiry", Integer.toString(expireTimeInSeconds));
        return request;
    }

    protected Request deleteSubscriptionRequest(String subscriptionName) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_STREAMS, name,
                "subscriptions", subscriptionName);
        return request;
    }
}
