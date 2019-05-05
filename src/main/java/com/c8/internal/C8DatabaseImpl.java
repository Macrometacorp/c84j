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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.arangodb.velocypack.Type;
import com.c8.C8Collection;
import com.c8.C8Cursor;
import com.c8.C8DBException;
import com.c8.C8Database;
import com.c8.C8Graph;
import com.c8.C8Route;
import com.c8.C8Search;
import com.c8.C8View;
import com.c8.entity.C8qlExecutionExplainEntity;
import com.c8.entity.C8qlFunctionEntity;
import com.c8.entity.C8qlParseEntity;
import com.c8.entity.C8DBVersion;
import com.c8.entity.CollectionEntity;
import com.c8.entity.CursorEntity;
import com.c8.entity.DatabaseEntity;
import com.c8.entity.EdgeDefinition;
import com.c8.entity.GraphEntity;
import com.c8.entity.IndexEntity;
import com.c8.entity.Permissions;
import com.c8.entity.QueryCachePropertiesEntity;
import com.c8.entity.QueryEntity;
import com.c8.entity.QueryTrackingPropertiesEntity;
import com.c8.entity.TraversalEntity;
import com.c8.entity.ViewEntity;
import com.c8.entity.ViewType;
import com.c8.internal.cursor.C8CursorImpl;
import com.c8.internal.net.HostHandle;
import com.c8.internal.util.DocumentUtil;
import com.c8.model.C8qlFunctionCreateOptions;
import com.c8.model.C8qlFunctionDeleteOptions;
import com.c8.model.C8qlFunctionGetOptions;
import com.c8.model.C8qlQueryExplainOptions;
import com.c8.model.C8qlQueryOptions;
import com.c8.model.CollectionCreateOptions;
import com.c8.model.CollectionsReadOptions;
import com.c8.model.DocumentReadOptions;
import com.c8.model.GraphCreateOptions;
import com.c8.model.TransactionOptions;
import com.c8.model.TraversalOptions;
import com.c8.model.c8search.C8SearchCreateOptions;
import com.c8.util.C8CursorInitializer;
import com.c8.velocystream.Request;

/**
 * 
 *
 */
public class C8DatabaseImpl extends InternalC8Database<C8DBImpl, C8ExecutorSync>
		implements C8Database {

	private C8CursorInitializer cursorInitializer;

	protected C8DatabaseImpl(final C8DBImpl c8DB, final String name) {
		super(c8DB, name);
	}

	@Override
	public C8DBVersion getVersion() throws C8DBException {
		return executor.execute(getVersionRequest(), C8DBVersion.class);
	}

	@Override
	public boolean exists() throws C8DBException {
		try {
			getInfo();
			return true;
		} catch (final C8DBException e) {
			if (C8Errors.ERROR_c8_DATABASE_NOT_FOUND.equals(e.getErrorNum())) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public Collection<String> getAccessibleDatabases() throws C8DBException {
		return executor.execute(getAccessibleDatabasesRequest(), getDatabaseResponseDeserializer());
	}

	@Override
	public C8Collection collection(final String name) {
		return new C8CollectionImpl(this, name);
	}

	@Override
	public CollectionEntity createCollection(final String name) throws C8DBException {
		return executor.execute(createCollectionRequest(name, new CollectionCreateOptions()), CollectionEntity.class);
	}

	@Override
	public CollectionEntity createCollection(final String name, final CollectionCreateOptions options)
			throws C8DBException {
		return executor.execute(createCollectionRequest(name, options), CollectionEntity.class);
	}

	@Override
	public Collection<CollectionEntity> getCollections() throws C8DBException {
		return executor.execute(getCollectionsRequest(new CollectionsReadOptions()),
			getCollectionsResponseDeserializer());
	}

	@Override
	public Collection<CollectionEntity> getCollections(final CollectionsReadOptions options) throws C8DBException {
		return executor.execute(getCollectionsRequest(options), getCollectionsResponseDeserializer());
	}

	@Override
	public IndexEntity getIndex(final String id) throws C8DBException {
		DocumentUtil.validateIndexId(id);
		final String[] split = id.split("/");
		return collection(split[0]).getIndex(split[1]);
	}

	@Override
	public String deleteIndex(final String id) throws C8DBException {
		DocumentUtil.validateIndexId(id);
		final String[] split = id.split("/");
		return collection(split[0]).deleteIndex(split[1]);
	}

	@Override
	public Boolean create() throws C8DBException {
		return c8().createDatabase(name());
	}

	@Override
	public Boolean drop() throws C8DBException {
		return executor.execute(dropRequest(), createDropResponseDeserializer());
	}

	@Override
	public void grantAccess(final String user, final Permissions permissions) throws C8DBException {
		executor.execute(grantAccessRequest(user, permissions), Void.class);
	}

	@Override
	public void grantAccess(final String user) throws C8DBException {
		executor.execute(grantAccessRequest(user, Permissions.RW), Void.class);
	}

	@Override
	public void revokeAccess(final String user) throws C8DBException {
		executor.execute(grantAccessRequest(user, Permissions.NONE), Void.class);
	}

	@Override
	public void resetAccess(final String user) throws C8DBException {
		executor.execute(resetAccessRequest(user), Void.class);
	}

	@Override
	public void grantDefaultCollectionAccess(final String user, final Permissions permissions)
			throws C8DBException {
		executor.execute(updateUserDefaultCollectionAccessRequest(user, permissions), Void.class);
	}

	@Override
	public Permissions getPermissions(final String user) throws C8DBException {
		return executor.execute(getPermissionsRequest(user), getPermissionsResponseDeserialzer());
	}

	@Override
	public <T> C8Cursor<T> query(
		final String query,
		final Map<String, Object> bindVars,
		final C8qlQueryOptions options,
		final Class<T> type) throws C8DBException {
		final Request request = queryRequest(query, bindVars, options);
		final HostHandle hostHandle = new HostHandle();
		final CursorEntity result = executor.execute(request, CursorEntity.class, hostHandle);
		return createCursor(result, type, options, hostHandle);
	}

	@Override
	public <T> C8Cursor<T> query(final String query, final Map<String, Object> bindVars, final Class<T> type)
			throws C8DBException {
		return query(query, bindVars, null, type);
	}

	@Override
	public <T> C8Cursor<T> query(final String query, final C8qlQueryOptions options, final Class<T> type)
			throws C8DBException {
		return query(query, null, options, type);
	}

	@Override
	public <T> C8Cursor<T> query(final String query, final Class<T> type) throws C8DBException {
		return query(query, null, null, type);
	}

	@Override
	public <T> C8Cursor<T> cursor(final String cursorId, final Class<T> type) throws C8DBException {
		final HostHandle hostHandle = new HostHandle();
		final CursorEntity result = executor.execute(queryNextRequest(cursorId, null), CursorEntity.class, hostHandle);
		return createCursor(result, type, null, hostHandle);
	}

	private <T> C8Cursor<T> createCursor(
		final CursorEntity result,
		final Class<T> type,
		final C8qlQueryOptions options,
		final HostHandle hostHandle) {
		final C8CursorExecute execute = new C8CursorExecute() {
			@Override
			public CursorEntity next(final String id) {
				return executor.execute(queryNextRequest(id, options), CursorEntity.class, hostHandle);
			}

			@Override
			public void close(final String id) {
				executor.execute(queryCloseRequest(id, options), Void.class, hostHandle);
			}
		};
		return cursorInitializer != null ? cursorInitializer.createInstance(this, execute, type, result)
				: new C8CursorImpl<T>(this, execute, type, result);
	}

	@Override
	public C8qlExecutionExplainEntity explainQuery(
		final String query,
		final Map<String, Object> bindVars,
		final C8qlQueryExplainOptions options) throws C8DBException {
		return executor.execute(explainQueryRequest(query, bindVars, options), C8qlExecutionExplainEntity.class);
	}

	@Override
	public C8qlParseEntity parseQuery(final String query) throws C8DBException {
		return executor.execute(parseQueryRequest(query), C8qlParseEntity.class);
	}

	@Override
	public void clearQueryCache() throws C8DBException {
		executor.execute(clearQueryCacheRequest(), Void.class);
	}

	@Override
	public QueryCachePropertiesEntity getQueryCacheProperties() throws C8DBException {
		return executor.execute(getQueryCachePropertiesRequest(), QueryCachePropertiesEntity.class);
	}

	@Override
	public QueryCachePropertiesEntity setQueryCacheProperties(final QueryCachePropertiesEntity properties)
			throws C8DBException {
		return executor.execute(setQueryCachePropertiesRequest(properties), QueryCachePropertiesEntity.class);
	}

	@Override
	public QueryTrackingPropertiesEntity getQueryTrackingProperties() throws C8DBException {
		return executor.execute(getQueryTrackingPropertiesRequest(), QueryTrackingPropertiesEntity.class);
	}

	@Override
	public QueryTrackingPropertiesEntity setQueryTrackingProperties(final QueryTrackingPropertiesEntity properties)
			throws C8DBException {
		return executor.execute(setQueryTrackingPropertiesRequest(properties), QueryTrackingPropertiesEntity.class);
	}

	@Override
	public Collection<QueryEntity> getCurrentlyRunningQueries() throws C8DBException {
		return executor.execute(getCurrentlyRunningQueriesRequest(), new Type<Collection<QueryEntity>>() {
		}.getType());
	}

	@Override
	public Collection<QueryEntity> getSlowQueries() throws C8DBException {
		return executor.execute(getSlowQueriesRequest(), new Type<Collection<QueryEntity>>() {
		}.getType());
	}

	@Override
	public void clearSlowQueries() throws C8DBException {
		executor.execute(clearSlowQueriesRequest(), Void.class);
	}

	@Override
	public void killQuery(final String id) throws C8DBException {
		executor.execute(killQueryRequest(id), Void.class);
	}

	@Override
	public void createAqlFunction(final String name, final String code, final C8qlFunctionCreateOptions options)
			throws C8DBException {
		executor.execute(createAqlFunctionRequest(name, code, options), Void.class);
	}

	@Override
	public Integer deleteAqlFunction(final String name, final C8qlFunctionDeleteOptions options)
			throws C8DBException {
		return executor.execute(deleteAqlFunctionRequest(name, options), deleteAqlFunctionResponseDeserializer());
	}

	@Override
	public Collection<C8qlFunctionEntity> getAqlFunctions(final C8qlFunctionGetOptions options) throws C8DBException {
		return executor.execute(getAqlFunctionsRequest(options), getAqlFunctionsResponseDeserializer());
	}

	@Override
	public C8Graph graph(final String name) {
		return new C8GraphImpl(this, name);
	}

	@Override
	public GraphEntity createGraph(final String name, final Collection<EdgeDefinition> edgeDefinitions)
			throws C8DBException {
		return executor.execute(createGraphRequest(name, edgeDefinitions, new GraphCreateOptions()),
			createGraphResponseDeserializer());
	}

	@Override
	public GraphEntity createGraph(
		final String name,
		final Collection<EdgeDefinition> edgeDefinitions,
		final GraphCreateOptions options) throws C8DBException {
		return executor.execute(createGraphRequest(name, edgeDefinitions, options), createGraphResponseDeserializer());
	}

	@Override
	public Collection<GraphEntity> getGraphs() throws C8DBException {
		return executor.execute(getGraphsRequest(), getGraphsResponseDeserializer());
	}

	@Override
	public <T> T transaction(final String action, final Class<T> type, final TransactionOptions options)
			throws C8DBException {
		return executor.execute(transactionRequest(action, options), transactionResponseDeserializer(type));
	}

	@Override
	public DatabaseEntity getInfo() throws C8DBException {
		return executor.execute(getInfoRequest(), getInfoResponseDeserializer());
	}

	@Override
	public <V, E> TraversalEntity<V, E> executeTraversal(
		final Class<V> vertexClass,
		final Class<E> edgeClass,
		final TraversalOptions options) throws C8DBException {
		final Request request = executeTraversalRequest(options);
		return executor.execute(request, executeTraversalResponseDeserializer(vertexClass, edgeClass));
	}

	@Override
	public <T> T getDocument(final String id, final Class<T> type) throws C8DBException {
		DocumentUtil.validateDocumentId(id);
		final String[] split = id.split("/");
		return collection(split[0]).getDocument(split[1], type);
	}

	@Override
	public <T> T getDocument(final String id, final Class<T> type, final DocumentReadOptions options)
			throws C8DBException {
		DocumentUtil.validateDocumentId(id);
		final String[] split = id.split("/");
		return collection(split[0]).getDocument(split[1], type, options);
	}

	@Override
	public void reloadRouting() throws C8DBException {
		executor.execute(reloadRoutingRequest(), Void.class);
	}

	protected C8DatabaseImpl setCursorInitializer(final C8CursorInitializer cursorInitializer) {
		this.cursorInitializer = cursorInitializer;
		return this;
	}

	@Override
	public C8Route route(final String... path) {
		return new C8RouteImpl(this, createPath(path), Collections.<String, String> emptyMap());
	}

	@Override
	public Collection<ViewEntity> getViews() throws C8DBException {
		return executor.execute(getViewsRequest(), getViewsResponseDeserializer());
	}

	@Override
	public C8View view(final String name) {
		return new C8ViewImpl(this, name);
	}

	@Override
	public C8Search c8Search(final String name) {
		return new C8SearchImpl(this, name);
	}

	@Override
	public ViewEntity createView(final String name, final ViewType type) throws C8DBException {
		return executor.execute(createViewRequest(name, type), ViewEntity.class);
	}

	@Override
	public ViewEntity createC8Search(final String name, final C8SearchCreateOptions options)
			throws C8DBException {
		return executor.execute(createC8SearchRequest(name, options), ViewEntity.class);
	}

}
