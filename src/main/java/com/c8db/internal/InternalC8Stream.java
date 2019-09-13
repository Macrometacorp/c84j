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

    protected static final String PATH_API_STREAMS = "/streams/persistent/stream";

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

    protected Request dropRequest() {
        return dropRequest(false);
    }

    protected Request dropRequest(final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_STREAMS, name);
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request getC8StreamBacklogRequest(final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name, "backlog");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected ResponseDeserializer<C8StreamBacklogEntity> getC8StreamBacklogResponseDeserializer() {
        return new ResponseDeserializer<C8StreamBacklogEntity>() {
            @Override
            public C8StreamBacklogEntity deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody().get(C8ResponseField.RESULT);
                try {
                    return new ObjectMapper().readValue(body.getAsString(), C8StreamBacklogEntity.class);
                } catch (IOException e) {
                    throw new C8DBException(e.getMessage());
                }
            }
        };
    }

    protected Request getC8StreamStatisticsRequest(final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name, "stats");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected ResponseDeserializer<C8StreamStatisticsEntity> getC8StreamStatisticsResponseDeserializer() {
        return new ResponseDeserializer<C8StreamStatisticsEntity>() {
            @Override
            public C8StreamStatisticsEntity deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody().get(C8ResponseField.RESULT);
                try {
                    return new ObjectMapper().readValue(body.getAsString(), C8StreamStatisticsEntity.class);
                } catch (IOException e) {
                    throw new C8DBException(e.getMessage());
                }
            }
        };
    }

    protected Request terminateC8StreamRequest(final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name, "terminate");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected ResponseDeserializer<Boolean> booleanResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().getAsBoolean();
            }
        };
    }

    protected Request getC8StreamSubscriptionsRequest(boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_STREAMS, name,
                "subscriptions");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected ResponseDeserializer<Collection<String>> getC8StreamSubscriptionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                try {
                    return new ObjectMapper().readValue(result.getAsString(), new TypeReference<Collection<String>>() {
                    });
                } catch (IOException e) {
                    throw new C8DBException(e.getMessage());
                }
            }
        };
    }

    protected Request skipMessagesRequest(final String subscriptionName, final int numberOfMessages,
            final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "subscription", subscriptionName, "skip", Integer.toString(numberOfMessages));
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request skipAllMessagesRequest(final String subscriptionName, final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "subscription", subscriptionName, "skip_all");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request resetCursorRequest(final String subscriptionName, final int timestamp, final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "subscription", subscriptionName, "resetcursor", Integer.toString(timestamp));
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request resetCursorRequest(final String subscriptionName, final boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "subscription", subscriptionName, "resetcursor");
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request expireMessagesRequest(String subscriptionName, int expireTimeInSeconds, boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_STREAMS, name,
                "subscription", subscriptionName, "expireMessages", Integer.toString(expireTimeInSeconds));
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }

    protected Request deleteSubscriptionRequest(String subscriptionName, boolean isLocal) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_STREAMS, name,
                "subscription", subscriptionName);
        if (isLocal) {
            request.putQueryParam("local", isLocal);
        }
        return request;
    }
}
