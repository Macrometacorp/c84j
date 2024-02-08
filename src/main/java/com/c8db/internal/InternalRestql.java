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
 * Modifications copyright (c) 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.GraphEntity;
import com.c8db.entity.UserQueryEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public abstract class InternalRestql<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_RESTQL = "/restql";
    protected static final String VERTEX = "vertex";
    protected static final String EDGE = "edge";
    private static final String GRAPH = "graph";

    private final D db;

    protected InternalRestql(final D db) {
        super(db.executor, db.util, db.context, db.tenant());
        this.db = db;
    }

    public D db() {
        return db;
    }

    protected Request dropRequest(final String name) {
        return dropRequest(name, null);
    }

    protected Request dropRequest(final String name, final String user) {
        if (user == null) {
            return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_RESTQL, name);
        } else {
            return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_RESTQL, name, user);
        }
    }

    protected ResponseDeserializer<Collection<UserQueryEntity>> getUserQueriesResponseDeserializer() {
        return new ResponseDeserializer<Collection<UserQueryEntity>>() {
            @Override
            public Collection<UserQueryEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT), new Type<Collection<UserQueryEntity>>() {
                }.getType());
            }
        };
    }

    protected Request getUserQueriesRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_RESTQL, "user");
    }

    protected Request getUserQueriesRequest(final String userName) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_RESTQL, "user", userName);
    }

    protected ResponseDeserializer<Collection<String>> getVertexCollectionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("collections"), new Type<Collection<String>>() {
                }.getType());
            }
        };
    }

    protected ResponseDeserializer<Collection<String>> getEdgeDefinitionsDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT), new Type<Collection<UserQueryEntity>>() {
                }.getType());
            }
        };
    }

    protected ResponseDeserializer<GraphEntity> addEdgeDefinitionResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(GRAPH), GraphEntity.class);
            }
        };
    }

    protected ResponseDeserializer<GraphEntity> replaceEdgeDefinitionResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(GRAPH), GraphEntity.class);
            }
        };
    }

    protected ResponseDeserializer<GraphEntity> removeEdgeDefinitionResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(GRAPH), GraphEntity.class);
            }
        };
    }

}
