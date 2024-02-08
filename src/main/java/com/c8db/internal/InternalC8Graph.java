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

import java.util.Collection;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.GraphEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.VertexCollectionCreateOptions;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 *
 */
public abstract class InternalC8Graph<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_GHARIAL = "/_api/graph";
    protected static final String VERTEX = "vertex";
    protected static final String EDGE = "edge";
    private static final String GRAPH = "graph";

    private final D db;
    private final String name;

    protected InternalC8Graph(final D db, final String name) {
        super(db.executor, db.util, db.context, db.tenant());
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

    protected Request dropRequest(final boolean dropCollections) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_GHARIAL, name);
        if (dropCollections) {
            request.putQueryParam("dropCollections", dropCollections);
        }
        return request;
    }

    protected Request getInfoRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_GHARIAL, name);
    }

    protected ResponseDeserializer<GraphEntity> getInfoResponseDeserializer() {
        return addVertexCollectionResponseDeserializer();
    }

    protected Request getVertexCollectionsRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_GHARIAL, name, VERTEX);
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

    protected Request addVertexCollectionRequest(final String name) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_GHARIAL, name(), VERTEX);
        request.setBody(util().serialize(OptionsBuilder.build(new VertexCollectionCreateOptions(), name)));
        return request;
    }

    protected ResponseDeserializer<GraphEntity> addVertexCollectionResponseDeserializer() {
        return addEdgeDefinitionResponseDeserializer();
    }

    protected Request getEdgeDefinitionsRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_GHARIAL, name, EDGE);
    }

    protected ResponseDeserializer<Collection<String>> getEdgeDefinitionsDeserializer() {
        return new ResponseDeserializer<Collection<String>>() {
            @Override
            public Collection<String> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("collections"), new Type<Collection<String>>() {
                }.getType());
            }
        };
    }

    protected Request addEdgeDefinitionRequest(final EdgeDefinition definition) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_GHARIAL, name, EDGE);
        request.setBody(util().serialize(definition));
        return request;
    }

    protected ResponseDeserializer<GraphEntity> addEdgeDefinitionResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(GRAPH), GraphEntity.class);
            }
        };
    }

    protected Request replaceEdgeDefinitionRequest(final EdgeDefinition definition) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_GHARIAL, name, EDGE,
                definition.getCollection());
        request.setBody(util().serialize(definition));
        return request;
    }

    protected ResponseDeserializer<GraphEntity> replaceEdgeDefinitionResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(GRAPH), GraphEntity.class);
            }
        };
    }

    protected Request removeEdgeDefinitionRequest(final String definitionName) {
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_GHARIAL, name, EDGE, definitionName);
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
