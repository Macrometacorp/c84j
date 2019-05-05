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

package com.c8.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.entity.C8qlFunctionEntity;
import com.c8.entity.CollectionEntity;
import com.c8.entity.DatabaseEntity;
import com.c8.entity.EdgeDefinition;
import com.c8.entity.GraphEntity;
import com.c8.entity.PathEntity;
import com.c8.entity.Permissions;
import com.c8.entity.QueryCachePropertiesEntity;
import com.c8.entity.QueryTrackingPropertiesEntity;
import com.c8.entity.TraversalEntity;
import com.c8.entity.ViewEntity;
import com.c8.entity.ViewType;
import com.c8.internal.C8Executor.ResponseDeserializer;
import com.c8.internal.util.RequestUtils;
import com.c8.internal.util.C8SerializationFactory.Serializer;
import com.c8.model.C8qlFunctionCreateOptions;
import com.c8.model.C8qlFunctionDeleteOptions;
import com.c8.model.C8qlFunctionGetOptions;
import com.c8.model.C8qlQueryExplainOptions;
import com.c8.model.C8qlQueryOptions;
import com.c8.model.C8qlQueryParseOptions;
import com.c8.model.CollectionCreateOptions;
import com.c8.model.CollectionsReadOptions;
import com.c8.model.GraphCreateOptions;
import com.c8.model.OptionsBuilder;
import com.c8.model.TransactionOptions;
import com.c8.model.TraversalOptions;
import com.c8.model.UserAccessOptions;
import com.c8.model.ViewCreateOptions;
import com.c8.model.c8search.C8SearchCreateOptions;
import com.c8.model.c8search.C8SearchOptionsBuilder;
import com.c8.util.C8Serializer;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public abstract class InternalC8Database<A extends InternalC8DB<E>, E extends C8Executor>
		extends C8Executeable<E> {

	protected static final String PATH_API_DATABASE = "/_api/database";
	private static final String PATH_API_VERSION = "/_api/version";
	private static final String PATH_API_CURSOR = "/_api/cursor";
	private static final String PATH_API_TRANSACTION = "/_api/transaction";
	private static final String PATH_API_AQLFUNCTION = "/_api/aqlfunction";
	private static final String PATH_API_EXPLAIN = "/_api/explain";
	private static final String PATH_API_QUERY = "/_api/query";
	private static final String PATH_API_QUERY_CACHE = "/_api/query-cache";
	private static final String PATH_API_QUERY_CACHE_PROPERTIES = "/_api/query-cache/properties";
	private static final String PATH_API_QUERY_PROPERTIES = "/_api/query/properties";
	private static final String PATH_API_QUERY_CURRENT = "/_api/query/current";
	private static final String PATH_API_QUERY_SLOW = "/_api/query/slow";
	private static final String PATH_API_TRAVERSAL = "/_api/traversal";
	private static final String PATH_API_ADMIN_ROUTING_RELOAD = "/_admin/routing/reload";
	private static final String PATH_API_USER = "/_api/user";

	private final String name;
	private final A c8;

	protected InternalC8Database(final A c8, final String name) {
		super(c8.executor, c8.util, c8.context);
		this.c8 = c8;
		this.name = name;
	}

	public A c8() {
		return c8;
	}

	public String name() {
		return name;
	}

	protected ResponseDeserializer<Collection<String>> getDatabaseResponseDeserializer() {
		return c8.getDatabaseResponseDeserializer();
	}

	protected Request getAccessibleDatabasesRequest() {
		return request(name, RequestType.GET, PATH_API_DATABASE, "user");
	}

	protected Request getVersionRequest() {
		return request(name, RequestType.GET, PATH_API_VERSION);
	}

	protected Request createCollectionRequest(final String name, final CollectionCreateOptions options) {
		return request(name(), RequestType.POST, InternalC8Collection.PATH_API_COLLECTION).setBody(
			util().serialize(OptionsBuilder.build(options != null ? options : new CollectionCreateOptions(), name)));
	}

	protected Request getCollectionsRequest(final CollectionsReadOptions options) {
		final Request request;
		request = request(name(), RequestType.GET, InternalC8Collection.PATH_API_COLLECTION);
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
		return request(C8RequestParam.SYSTEM, RequestType.DELETE, PATH_API_DATABASE, name);
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
		return request(C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
			name).setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
	}

	protected Request resetAccessRequest(final String user) {
		return request(C8RequestParam.SYSTEM, RequestType.DELETE, PATH_API_USER, user, C8RequestParam.DATABASE,
			name);
	}

	protected Request updateUserDefaultCollectionAccessRequest(final String user, final Permissions permissions) {
		return request(C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
			name, "*").setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
	}

	protected Request getPermissionsRequest(final String user) {
		return request(C8RequestParam.SYSTEM, RequestType.GET, PATH_API_USER, user, C8RequestParam.DATABASE,
			name);
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

	protected Request queryRequest(
		final String query,
		final Map<String, Object> bindVars,
		final C8qlQueryOptions options) {
		final C8qlQueryOptions opt = options != null ? options : new C8qlQueryOptions();
		final Request request = request(name, RequestType.POST, PATH_API_CURSOR).setBody(
			util().serialize(OptionsBuilder.build(opt, query, bindVars != null
					? util().serialize(bindVars, new C8Serializer.Options().serializeNullValues(true)) : null)));
		if (opt.getAllowDirtyRead() == Boolean.TRUE) {
			RequestUtils.allowDirtyRead(request);
		}
		return request;
	}

	protected Request queryNextRequest(final String id, final C8qlQueryOptions options) {
		final Request request = request(name, RequestType.PUT, PATH_API_CURSOR, id);
		final C8qlQueryOptions opt = options != null ? options : new C8qlQueryOptions();
		if (opt.getAllowDirtyRead() == Boolean.TRUE) {
			RequestUtils.allowDirtyRead(request);
		}
		return request;
	}

	protected Request queryCloseRequest(final String id, final C8qlQueryOptions options) {
		final Request request = request(name, RequestType.DELETE, PATH_API_CURSOR, id);
		final C8qlQueryOptions opt = options != null ? options : new C8qlQueryOptions();
		if (opt.getAllowDirtyRead() == Boolean.TRUE) {
			RequestUtils.allowDirtyRead(request);
		}
		return request;
	}

	protected Request explainQueryRequest(
		final String query,
		final Map<String, Object> bindVars,
		final C8qlQueryExplainOptions options) {
		return request(name, RequestType.POST, PATH_API_EXPLAIN).setBody(util().serialize(
			OptionsBuilder.build(options != null ? options : new C8qlQueryExplainOptions(), query, bindVars)));
	}

	protected Request parseQueryRequest(final String query) {
		return request(name, RequestType.POST, PATH_API_QUERY)
				.setBody(util().serialize(OptionsBuilder.build(new C8qlQueryParseOptions(), query)));
	}

	protected Request clearQueryCacheRequest() {
		return request(name, RequestType.DELETE, PATH_API_QUERY_CACHE);
	}

	protected Request getQueryCachePropertiesRequest() {
		return request(name, RequestType.GET, PATH_API_QUERY_CACHE_PROPERTIES);
	}

	protected Request setQueryCachePropertiesRequest(final QueryCachePropertiesEntity properties) {
		return request(name, RequestType.PUT, PATH_API_QUERY_CACHE_PROPERTIES).setBody(util().serialize(properties));
	}

	protected Request getQueryTrackingPropertiesRequest() {
		return request(name, RequestType.GET, PATH_API_QUERY_PROPERTIES);
	}

	protected Request setQueryTrackingPropertiesRequest(final QueryTrackingPropertiesEntity properties) {
		return request(name, RequestType.PUT, PATH_API_QUERY_PROPERTIES).setBody(util().serialize(properties));
	}

	protected Request getCurrentlyRunningQueriesRequest() {
		return request(name, RequestType.GET, PATH_API_QUERY_CURRENT);
	}

	protected Request getSlowQueriesRequest() {
		return request(name, RequestType.GET, PATH_API_QUERY_SLOW);
	}

	protected Request clearSlowQueriesRequest() {
		return request(name, RequestType.DELETE, PATH_API_QUERY_SLOW);
	}

	protected Request killQueryRequest(final String id) {
		return request(name, RequestType.DELETE, PATH_API_QUERY, id);
	}

	protected Request createAqlFunctionRequest(
		final String name,
		final String code,
		final C8qlFunctionCreateOptions options) {
		return request(name(), RequestType.POST, PATH_API_AQLFUNCTION).setBody(util().serialize(
			OptionsBuilder.build(options != null ? options : new C8qlFunctionCreateOptions(), name, code)));
	}

	protected Request deleteAqlFunctionRequest(final String name, final C8qlFunctionDeleteOptions options) {
		final Request request = request(name(), RequestType.DELETE, PATH_API_AQLFUNCTION, name);
		final C8qlFunctionDeleteOptions params = options != null ? options : new C8qlFunctionDeleteOptions();
		request.putQueryParam("group", params.getGroup());
		return request;
	}

	protected ResponseDeserializer<Integer> deleteAqlFunctionResponseDeserializer() {
		return new ResponseDeserializer<Integer>() {
			@Override
			public Integer deserialize(final Response response) throws VPackException {
				// compatibility with C8DB < 3.4
				// https://docs.c8db.com/devel/Manual/ReleaseNotes/UpgradingChanges34.html
				Integer count = null;
				final VPackSlice body = response.getBody();
				if (body.isObject()) {
					final VPackSlice deletedCount = body.get("deletedCount");
					if (deletedCount.isInteger()) {
						count = deletedCount.getAsInt();
					}
				}
				return count;
			};
		};
	}

	protected Request getAqlFunctionsRequest(final C8qlFunctionGetOptions options) {
		final Request request = request(name(), RequestType.GET, PATH_API_AQLFUNCTION);
		final C8qlFunctionGetOptions params = options != null ? options : new C8qlFunctionGetOptions();
		request.putQueryParam("namespace", params.getNamespace());
		return request;
	}

	protected ResponseDeserializer<Collection<C8qlFunctionEntity>> getAqlFunctionsResponseDeserializer() {
		return new ResponseDeserializer<Collection<C8qlFunctionEntity>>() {
			@Override
			public Collection<C8qlFunctionEntity> deserialize(final Response response) throws VPackException {
				final VPackSlice body = response.getBody();
				// compatibility with C8DB < 3.4
				// https://docs.c8db.com/devel/Manual/ReleaseNotes/UpgradingChanges34.html
				final VPackSlice result = body.isArray() ? body : body.get(C8ResponseField.RESULT);
				return util().deserialize(result, new Type<Collection<C8qlFunctionEntity>>() {
				}.getType());
			}
		};
	}

	protected Request createGraphRequest(
		final String name,
		final Collection<EdgeDefinition> edgeDefinitions,
		final GraphCreateOptions options) {
		return request(name(), RequestType.POST, InternalC8Graph.PATH_API_GHARIAL).setBody(util().serialize(
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
		return request(name, RequestType.GET, InternalC8Graph.PATH_API_GHARIAL);
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

	protected Request transactionRequest(final String action, final TransactionOptions options) {
		return request(name, RequestType.POST, PATH_API_TRANSACTION).setBody(
			util().serialize(OptionsBuilder.build(options != null ? options : new TransactionOptions(), action)));
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

	protected Request getInfoRequest() {
		return request(name, RequestType.GET, PATH_API_DATABASE, "current");
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
		return request(name, RequestType.POST, PATH_API_TRAVERSAL)
				.setBody(util().serialize(options != null ? options : new TransactionOptions()));
	}

	@SuppressWarnings("hiding")
	protected <E, V> ResponseDeserializer<TraversalEntity<V, E>> executeTraversalResponseDeserializer(
		final Class<V> vertexClass,
		final Class<E> edgeClass) {
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

	protected Request reloadRoutingRequest() {
		return request(name, RequestType.POST, PATH_API_ADMIN_ROUTING_RELOAD);
	}

	protected Request getViewsRequest() {
		return request(name, RequestType.GET, InternalC8View.PATH_API_VIEW);
	}

	protected ResponseDeserializer<Collection<ViewEntity>> getViewsResponseDeserializer() {
		return new ResponseDeserializer<Collection<ViewEntity>>() {
			@Override
			public Collection<ViewEntity> deserialize(final Response response) throws VPackException {
				final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
				return util().deserialize(result, new Type<Collection<ViewEntity>>() {
				}.getType());
			}
		};
	}

	protected Request createViewRequest(final String name, final ViewType type) {
		return request(name(), RequestType.POST, InternalC8View.PATH_API_VIEW)
				.setBody(util().serialize(OptionsBuilder.build(new ViewCreateOptions(), name, type)));
	}

	protected Request createC8SearchRequest(final String name, final C8SearchCreateOptions options) {
		return request(name(), RequestType.POST, InternalC8View.PATH_API_VIEW).setBody(util().serialize(
			C8SearchOptionsBuilder.build(options != null ? options : new C8SearchCreateOptions(), name)));
	}
}
