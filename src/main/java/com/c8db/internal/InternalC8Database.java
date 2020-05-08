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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.C8StreamEntity;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.DatabaseEntity;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.GraphEntity;
import com.c8db.entity.PathEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.QueryTrackingPropertiesEntity;
import com.c8db.entity.StreamTransactionEntity;
import com.c8db.entity.TransactionEntity;
import com.c8db.entity.TraversalEntity;
import com.c8db.entity.UserQuery;
import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.C8StreamCreateOptions;
import com.c8db.model.C8TransactionOptions;
import com.c8db.model.C8qlQueryExplainOptions;
import com.c8db.model.C8qlQueryOptions;
import com.c8db.model.C8qlQueryParseOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.CollectionsReadOptions;
import com.c8db.model.GraphCreateOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.StreamTransactionOptions;
import com.c8db.model.TraversalOptions;
import com.c8db.model.UserAccessOptions;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

/**
 */
public abstract class InternalC8Database<A extends InternalC8DB<E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_DATABASE = "/database";
    protected static final String PATH_API_DCLIST = "/datacenter";
    protected static final String PATH_API_TENANT = "/_tenant";
    protected static final String PATH_API_USER = "/_admin/user";
    protected static final String PATH_API_VERSION = "/_admin/version";
    protected static final String PATH_API_STREAMS = "/streams";
    protected static final String PATH_API_PERSISTENT = "persistent";
    protected static final String PATH_API_STREAM = "stream";
    protected static final String PATH_API_TRANSACTION = "/transaction";
    protected static final String PATH_API_CURSOR = "/cursor";
    protected static final String PATH_API_QUERY = "/query";
    protected static final String PATH_API_QUERY_CURRENT = "/query/current";
    protected static final String PATH_API_EXPLAIN = "explain";
    protected static final String PATH_API_QUERY_SLOW = "/query/slow";
    protected static final String PATH_API_QUERY_PROPERTIES = "/query/properties";
    protected static final String PATH_API_USER_QUERIES = "/restql";

    private static final String PATH_API_BEGIN_STREAM_TRANSACTION = "/_api/transaction/begin";
    private static final String PATH_API_TRAVERSAL = "/_api/traversal";

    private final String tenant;
    private final String name;
    private final String spotDc;
    private final String dcList;
    private final A c8db;

    protected InternalC8Database(final A c8db, final String tenant, final String name, final String spotDc,
            final String dcList) {
        super(c8db.executor, c8db.util, c8db.context);
        this.c8db = c8db;
        this.tenant = tenant;
        this.name = name;
        this.spotDc = spotDc;
        this.dcList = dcList;
    }

    public A c8db() {
        return c8db;
    }

    public String tenant() {
        return tenant;
    }

    public String name() {
        return name;
    }

    public String spotDc() {
        return spotDc;
    }

    public String dcList() {
        return dcList;
    }

    protected ResponseDeserializer<Collection<String>> getDatabaseResponseDeserializer() {
        return c8db.getGeoFabricsResponseDeserializer();
    }

    protected ResponseDeserializer<Collection<String>> getAccessibleDatabasesForResponseDeserializer() {
        return c8db.getAccessibleGeoFabricsForResponseDeserializer();
    }

    protected Request getAccessibleDatabasesRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_DATABASE, "user");
    }

    protected Request getVersionRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_VERSION);
    }

    protected Request createCollectionRequest(final String name, final CollectionCreateOptions options) {

        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new CollectionCreateOptions(), name));

        return request(tenant(), name(), RequestType.POST, InternalC8Collection.PATH_API_COLLECTION).setBody(body);
    }

    protected Request getCollectionsRequest(final CollectionsReadOptions options) {
        final Request request;
        request = request(tenant(), name(), RequestType.GET, InternalC8Collection.PATH_API_COLLECTION);
        final CollectionsReadOptions params = (options != null ? options : new CollectionsReadOptions());
        request.putQueryParam("excludeSystem", params.getExcludeSystem());
        return request;
    }

    protected ResponseDeserializer<Collection<CollectionEntity>> getCollectionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<CollectionEntity>>() {
            @Override
            public Collection<CollectionEntity> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
                return util().deserialize(result, new Type<Collection<CollectionEntity>>() {
                }.getType());
            }
        };
    }

    protected Request dropRequest() {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.DELETE, PATH_API_DATABASE,
                name);
    }

    protected ResponseDeserializer<Boolean> createDropResponseDeserializer() {
        return new ResponseDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(final Response response) throws VPackException {
                return response.getBody().get(C8ResponseField.RESULT).getAsBoolean();
            }
        };
    }

    protected Request grantAccessRequest(final String user, final Permissions permissions) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user,
                C8RequestParam.DATABASE, name)
                        .setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
    }

    protected Request resetAccessRequest(final String user) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.DELETE, PATH_API_USER,
                user, C8RequestParam.DATABASE, name);
    }

    protected Request getPermissionsRequest(final String user) {
        return request(C8RequestParam.DEMO_TENANT, C8RequestParam.SYSTEM, RequestType.GET, PATH_API_USER, user,
                C8RequestParam.DATABASE, name);
    }

    protected ResponseDeserializer<Permissions> getPermissionsResponseDeserialzer() {
        return new ResponseDeserializer<Permissions>() {
            @Override
            public Permissions deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                if (body != null) {
                    final VPackSlice result = body.get(C8ResponseField.RESULT);
                    if (!result.isNone()) {
                        return util().deserialize(result, Permissions.class);
                    }
                }
                return null;
            }
        };
    }

    protected Request queryRequest(final String query, final Map<String, Object> bindVars,
            final C8qlQueryOptions options) {
        final C8qlQueryOptions opt = options != null ? options : new C8qlQueryOptions();
        final Request request = request(tenant, name, RequestType.POST,
                PATH_API_CURSOR)
                        .setBody(
                                util().serialize(
                                        OptionsBuilder
                                                .build(opt, query,
                                                        bindVars != null
                                                                ? util(C8SerializationFactory.Serializer.CUSTOM)
                                                                        .serialize(bindVars,
                                                                                new C8Serializer.Options()
                                                                                        .serializeNullValues(true))
                                                                : null)));
        return request;
    }

    protected Request queryNextRequest(final String id, Map<String, String> meta) {

        final Request request = request(tenant, name, RequestType.PUT, PATH_API_CURSOR, id);

        if (meta != null) {
            request.getHeaderParam().putAll(meta);
        }
        return request;
    }

    protected Request queryCloseRequest(final String id, Map<String, String> meta) {

        final Request request = request(tenant, name, RequestType.DELETE, PATH_API_CURSOR, id);

        if (meta != null) {
            request.getHeaderParam().putAll(meta);
        }

        return request;
    }

    protected Request parseQueryRequest(final String query) {
        return request(tenant, name, RequestType.POST, PATH_API_QUERY)
                .setBody(util().serialize(OptionsBuilder.build(new C8qlQueryParseOptions(), query)));
    }

    protected Request getCurrentlyRunningQueriesRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_QUERY_CURRENT);
    }

    protected Request explainQueryRequest(final String query, final Map<String, Object> bindVars,
            final C8qlQueryExplainOptions options) {

        final C8qlQueryExplainOptions opt = options != null ? options : new C8qlQueryExplainOptions();

        return request(tenant, name, RequestType.POST, PATH_API_QUERY,
                PATH_API_EXPLAIN)
                        .setBody(
                                util().serialize(
                                        OptionsBuilder
                                                .build(opt, query,
                                                        bindVars != null
                                                                ? util(C8SerializationFactory.Serializer.CUSTOM)
                                                                        .serialize(bindVars,
                                                                                new C8Serializer.Options()
                                                                                        .serializeNullValues(true))
                                                                : null)));
    }

    protected Request getQueryTrackingPropertiesRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_QUERY_PROPERTIES);
    }

    protected Request setQueryTrackingPropertiesRequest(final QueryTrackingPropertiesEntity properties) {
        return request(tenant, name, RequestType.PUT, PATH_API_QUERY_PROPERTIES).setBody(util().serialize(properties));
    }

    protected Request clearSlowQueriesRequest() {
        return request(tenant, name, RequestType.DELETE, PATH_API_QUERY_SLOW);
    }

    protected Request getSlowQueriesRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_QUERY_SLOW);
    }

    protected Request killQueryRequest(final String id) {
        return request(tenant, name, RequestType.DELETE, PATH_API_QUERY, id);
    }

    protected Request createGraphRequest(final String name, final Collection<EdgeDefinition> edgeDefinitions,
            final GraphCreateOptions options) {
        return request(tenant(), name(), RequestType.POST, InternalC8Graph.PATH_API_GHARIAL).setBody(util().serialize(
                OptionsBuilder.build(options != null ? options : new GraphCreateOptions(), name, edgeDefinitions)));
    }

    protected ResponseDeserializer<GraphEntity> createGraphResponseDeserializer() {
        return new ResponseDeserializer<GraphEntity>() {
            @Override
            public GraphEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("graph"), GraphEntity.class);
            }
        };
    }

    protected Request getGraphsRequest() {
        return request(tenant, name, RequestType.GET, InternalC8Graph.PATH_API_GHARIAL);
    }

    protected ResponseDeserializer<Collection<GraphEntity>> getGraphsResponseDeserializer() {
        return new ResponseDeserializer<Collection<GraphEntity>>() {
            @Override
            public Collection<GraphEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("graphs"), new Type<Collection<GraphEntity>>() {
                }.getType());
            }
        };
    }

    protected Request transactionRequest(final String action, final C8TransactionOptions options) {
        return request(tenant, name, RequestType.POST, PATH_API_TRANSACTION).setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new C8TransactionOptions(), action)));
    }

    protected <T> ResponseDeserializer<T> transactionResponseDeserializer(final Class<T> type) {
        return new ResponseDeserializer<T>() {
            @Override
            public T deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                if (body != null) {
                    final VPackSlice result = body.get(C8ResponseField.RESULT);
                    if (!result.isNone() && !result.isNull()) {
                        return util(Serializer.CUSTOM).deserialize(result, type);
                    }
                }
                return null;
            }
        };
    }

    protected Request beginStreamTransactionRequest(final StreamTransactionOptions options) {
        return request(tenant, name, RequestType.POST, PATH_API_BEGIN_STREAM_TRANSACTION)
                .setBody(util().serialize(options != null ? options : new StreamTransactionOptions()));
    }

    protected Request abortStreamTransactionRequest(String id) {
        return request(tenant, name, RequestType.DELETE, PATH_API_TRANSACTION, id);
    }

    protected Request getStreamTransactionsRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_TRANSACTION);
    }

    protected Request getStreamTransactionRequest(String id) {
        return request(tenant, name, RequestType.GET, PATH_API_TRANSACTION, id);
    }

    protected ResponseDeserializer<Collection<TransactionEntity>> transactionsResponseDeserializer() {
        return new ResponseDeserializer<Collection<TransactionEntity>>() {
            @Override
            public Collection<TransactionEntity> deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody().get("transactions");
                return util().deserialize(result, new Type<Collection<TransactionEntity>>() {
                }.getType());
            }
        };
    }

    protected Request commitStreamTransactionRequest(String id) {
        return request(tenant, name, RequestType.PUT, PATH_API_TRANSACTION, id);
    }

    protected ResponseDeserializer<StreamTransactionEntity> streamTransactionResponseDeserializer() {
        return new ResponseDeserializer<StreamTransactionEntity>() {
            @Override
            public StreamTransactionEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT),
                        StreamTransactionEntity.class);
            }
        };
    }

    protected Request getInfoRequest() {
        return request(tenant, name, RequestType.GET, PATH_API_DATABASE, "current");
    }

    protected ResponseDeserializer<DatabaseEntity> getInfoResponseDeserializer() {
        return new ResponseDeserializer<DatabaseEntity>() {
            @Override
            public DatabaseEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT), DatabaseEntity.class);
            }
        };
    }

    protected Request executeTraversalRequest(final TraversalOptions options) {
        return request(tenant, name, RequestType.POST, PATH_API_TRAVERSAL)
                .setBody(util().serialize(options != null ? options : new C8TransactionOptions()));
    }

    @SuppressWarnings("hiding")
    protected <E, V> ResponseDeserializer<TraversalEntity<V, E>> executeTraversalResponseDeserializer(
            final Class<V> vertexClass, final Class<E> edgeClass) {
        return new ResponseDeserializer<TraversalEntity<V, E>>() {
            @Override
            public TraversalEntity<V, E> deserialize(final Response response) throws VPackException {
                final TraversalEntity<V, E> result = new TraversalEntity<V, E>();
                final VPackSlice visited = response.getBody().get(C8ResponseField.RESULT).get("visited");
                result.setVertices(deserializeVertices(vertexClass, visited));

                final Collection<PathEntity<V, E>> paths = new ArrayList<PathEntity<V, E>>();
                for (final Iterator<VPackSlice> iterator = visited.get("paths").arrayIterator(); iterator.hasNext();) {
                    final PathEntity<V, E> path = new PathEntity<V, E>();
                    final VPackSlice next = iterator.next();
                    path.setEdges(deserializeEdges(edgeClass, next));
                    path.setVertices(deserializeVertices(vertexClass, next));
                    paths.add(path);
                }
                result.setPaths(paths);
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <V> Collection<V> deserializeVertices(final Class<V> vertexClass, final VPackSlice vpack)
            throws VPackException {
        final Collection<V> vertices = new ArrayList<V>();
        for (final Iterator<VPackSlice> iterator = vpack.get("vertices").arrayIterator(); iterator.hasNext();) {
            vertices.add((V) util(Serializer.CUSTOM).deserialize(iterator.next(), vertexClass));
        }
        return vertices;
    }

    @SuppressWarnings({ "hiding", "unchecked" })
    protected <E> Collection<E> deserializeEdges(final Class<E> edgeClass, final VPackSlice next)
            throws VPackException {
        final Collection<E> edges = new ArrayList<E>();
        for (final Iterator<VPackSlice> iteratorEdge = next.get("edges").arrayIterator(); iteratorEdge.hasNext();) {
            edges.add((E) util(Serializer.CUSTOM).deserialize(iteratorEdge.next(), edgeClass));
        }
        return edges;
    }

    protected Request createC8PersistentStreamRequest(final String name, final C8StreamCreateOptions options) {
        Request request = request(tenant(), name(), RequestType.POST, PATH_API_STREAMS, PATH_API_PERSISTENT,
                PATH_API_STREAM, name);
        request.putQueryParam("local", options != null ? options.getIsLocal() : false);
        return request;
    }

    protected Request getC8PersistentStreamsRequest(final C8StreamCreateOptions options) {
        Request request = request(tenant(), name(), RequestType.GET, PATH_API_STREAMS, PATH_API_PERSISTENT);
        request.putQueryParam("local", options != null ? options.getIsLocal() : false);
        return request;
    }

    protected ResponseDeserializer<Collection<C8StreamEntity>> getC8StreamsResponseDeserializer() {
        return new ResponseDeserializer<Collection<C8StreamEntity>>() {
            @Override
            public Collection<C8StreamEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get(C8ResponseField.RESULT),
                        new Type<Collection<C8StreamEntity>>() {
                        }.getType());
            }
        };
    }

    protected Request getC8StreamsRequest() {
        return request(tenant(), name(), RequestType.GET, PATH_API_STREAMS);
    }

    protected Request clearC8StreamBacklogRequest() {
        return request(tenant(), name(), RequestType.POST, PATH_API_STREAMS, "clearbacklog");
    }

    protected Request clearC8StreamBacklogRequest(final String subscriptionName) {
        return request(tenant(), name(), RequestType.POST, PATH_API_STREAMS, "clearbacklog", subscriptionName);
    }

    protected Request unsubscribeRequest(final String subscriptionName) {
        return request(tenant(), name(), RequestType.POST, PATH_API_STREAMS, "unsubscribe", subscriptionName);
    }

    protected Request createUserQueryRequest(UserQueryOptions options) {
        Request request = request(tenant(), name(), RequestType.POST, PATH_API_USER_QUERIES);
        request.setBody(util().serialize(new UserQuery(options != null ? options : new UserQueryOptions()),
                new C8Serializer.Options().serializeNullValues(true)));
        return request;
    }

    protected ResponseDeserializer<UserQueryEntity> createUserQueryResponseDeserializer() {
        return new ResponseDeserializer<UserQueryEntity>() {
            @Override
            public UserQueryEntity deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("result"), UserQueryEntity.class);
            }
        };
    }

    protected Request userQueryRequest(final String userName, final String restqlName, final Map<String, Object> bindVars) {
        final Request request = userName == null ? request(tenant, name, RequestType.POST, PATH_API_USER_QUERIES, "execute",  restqlName)
                : request(tenant, name, RequestType.POST, PATH_API_USER_QUERIES, userName, name);
        request.setBody(util().serialize(bindVars == null ? new HashMap<String, Object>() : bindVars));
        return request;
    }

}
