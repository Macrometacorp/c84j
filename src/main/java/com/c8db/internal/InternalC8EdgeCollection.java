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

import java.util.HashMap;
import java.util.Map;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.DocumentField;
import com.c8db.entity.EdgeEntity;
import com.c8db.entity.EdgeUpdateEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.EdgeCreateOptions;
import com.c8db.model.EdgeDeleteOptions;
import com.c8db.model.EdgeReplaceOptions;
import com.c8db.model.EdgeUpdateOptions;
import com.c8db.model.GraphDocumentReadOptions;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 *
 */
public abstract class InternalC8EdgeCollection<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, G extends InternalC8Graph<A, D, E>, E extends C8Executor>
        extends C8Executeable<E> {

    private final G graph;
    private final String name;

    protected InternalC8EdgeCollection(final G graph, final String name) {
        super(graph.executor, graph.util, graph.context, graph.db().tenant());
        this.graph = graph;
        this.name = name;
    }

    public G graph() {
        return graph;
    }

    public String name() {
        return name;
    }

    protected <T> Request insertEdgeRequest(final T value, final EdgeCreateOptions options) {
        final Request request = request(graph.db().tenant(), graph.db().name(), RequestType.POST,
                InternalC8Graph.PATH_API_GHARIAL, graph.name(), InternalC8Graph.EDGE, name);
        final EdgeCreateOptions params = (options != null ? options : new EdgeCreateOptions());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected <T> ResponseDeserializer<EdgeEntity> insertEdgeResponseDeserializer(final T value) {
        return new ResponseDeserializer<EdgeEntity>() {
            @Override
            public EdgeEntity deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody().get(InternalC8Graph.EDGE);
                final EdgeEntity doc = util().deserialize(body, EdgeEntity.class);
                final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                values.put(DocumentField.Type.ID, doc.getId());
                values.put(DocumentField.Type.KEY, doc.getKey());
                values.put(DocumentField.Type.REV, doc.getRev());
                executor.documentCache().setValues(value, values);
                return doc;
            }
        };
    }

    protected Request getEdgeRequest(final String key, final GraphDocumentReadOptions options) {
        final Request request = request(graph.db().tenant(), graph.db().name(), RequestType.GET,
                InternalC8Graph.PATH_API_GHARIAL, graph.name(), InternalC8Graph.EDGE,
                DocumentUtil.createDocumentHandle(name, key));
        final GraphDocumentReadOptions params = (options != null ? options : new GraphDocumentReadOptions());
        request.putHeaderParam(C8RequestParam.IF_NONE_MATCH, params.getIfNoneMatch());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        return request;
    }

    protected <T> ResponseDeserializer<T> getEdgeResponseDeserializer(final Class<T> type) {
        return new ResponseDeserializer<T>() {
            @Override
            public T deserialize(final Response response) throws VPackException {
                return util(Serializer.CUSTOM).deserialize(response.getBody().get(InternalC8Graph.EDGE), type);
            }
        };
    }

    protected <T> Request replaceEdgeRequest(final String key, final T value, final EdgeReplaceOptions options) {
        final Request request = request(graph.db().tenant(), graph.db().name(), RequestType.PUT,
                InternalC8Graph.PATH_API_GHARIAL, graph.name(), InternalC8Graph.EDGE,
                DocumentUtil.createDocumentHandle(name, key));
        final EdgeReplaceOptions params = (options != null ? options : new EdgeReplaceOptions());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected <T> ResponseDeserializer<EdgeUpdateEntity> replaceEdgeResponseDeserializer(final T value) {
        return new ResponseDeserializer<EdgeUpdateEntity>() {
            @Override
            public EdgeUpdateEntity deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody().get(InternalC8Graph.EDGE);
                final EdgeUpdateEntity doc = util().deserialize(body, EdgeUpdateEntity.class);
                final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                values.put(DocumentField.Type.REV, doc.getRev());
                executor.documentCache().setValues(value, values);
                return doc;
            }
        };
    }

    protected <T> Request updateEdgeRequest(final String key, final T value, final EdgeUpdateOptions options) {
        final Request request;
        request = request(graph.db().tenant(), graph.db().name(), RequestType.PATCH,
                InternalC8Graph.PATH_API_GHARIAL, graph.name(), InternalC8Graph.EDGE,
                DocumentUtil.createDocumentHandle(name, key));
        final EdgeUpdateOptions params = (options != null ? options : new EdgeUpdateOptions());
        request.putQueryParam(C8RequestParam.KEEP_NULL, params.getKeepNull());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.setBody(
                util(Serializer.CUSTOM).serialize(value, new C8Serializer.Options().serializeNullValues(true)));
        return request;
    }

    protected <T> ResponseDeserializer<EdgeUpdateEntity> updateEdgeResponseDeserializer(final T value) {
        return new ResponseDeserializer<EdgeUpdateEntity>() {
            @Override
            public EdgeUpdateEntity deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody().get(InternalC8Graph.EDGE);
                final EdgeUpdateEntity doc = util().deserialize(body, EdgeUpdateEntity.class);
                final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                values.put(DocumentField.Type.REV, doc.getRev());
                executor.documentCache().setValues(value, values);
                return doc;
            }
        };
    }

    protected Request deleteEdgeRequest(final String key, final EdgeDeleteOptions options) {
        final Request request = request(graph.db().tenant(), graph.db().name(), RequestType.DELETE,
                InternalC8Graph.PATH_API_GHARIAL, graph.name(), InternalC8Graph.EDGE,
                DocumentUtil.createDocumentHandle(name, key));
        final EdgeDeleteOptions params = (options != null ? options : new EdgeDeleteOptions());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        return request;
    }

}
