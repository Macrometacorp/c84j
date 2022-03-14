/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.c8db.C8Admin;
import com.c8db.C8Alerts;
import com.c8db.C8ApiKeys;
import com.c8db.C8Collection;
import com.c8db.C8Cursor;
import com.c8db.C8DBException;
import com.c8db.C8Database;
import com.c8db.C8Event;
import com.c8db.C8Graph;
import com.c8db.C8Stream;
import com.c8db.Restql;
import com.c8db.Service;
import com.c8db.entity.C8DBVersion;
import com.c8db.entity.C8StreamEntity;
import com.c8db.entity.C8qlExecutionExplainEntity;
import com.c8db.entity.C8qlParseEntity;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.CursorEntity;
import com.c8db.entity.DatabaseEntity;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.GraphEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.QueryEntity;
import com.c8db.entity.QueryTrackingPropertiesEntity;
import com.c8db.entity.StreamTransactionEntity;
import com.c8db.entity.TransactionEntity;
import com.c8db.entity.TraversalEntity;
import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;
import com.c8db.internal.cursor.C8CursorImpl;
import com.c8db.internal.net.HostHandle;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.model.C8StreamCreateOptions;
import com.c8db.model.C8TransactionOptions;
import com.c8db.model.C8qlQueryExplainOptions;
import com.c8db.model.C8qlQueryOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.CollectionsReadOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.GraphCreateOptions;
import com.c8db.model.StreamTransactionOptions;
import com.c8db.model.TraversalOptions;
import com.c8db.util.C8CursorInitializer;
import com.c8db.velocystream.Request;

import java.util.Collection;
import java.util.Map;

public class C8DatabaseImpl extends InternalC8Database<C8DBImpl, C8ExecutorSync>
        implements C8Database {

    private C8CursorInitializer cursorInitializer;

    protected C8DatabaseImpl(final C8DBImpl c8DB, final String tenant, final String name,
                             final String spotDc, final String dcList) {
        super(c8DB, tenant, name, spotDc, dcList);
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
            if (C8Errors.ERROR_C8_DATABASE_NOT_FOUND.equals(e.getErrorNum())) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public Collection<String> getAccessibleGeoFabrics() throws C8DBException {
        return executor.execute(getAccessibleDatabasesRequest(), getAccessibleDatabasesForResponseDeserializer());
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
        return c8db().createGeoFabric(tenant(), name(), spotDc(), dcList(), name());
    }

    @Override
    public Boolean create(String geoFabric) throws C8DBException {
        return c8db().createGeoFabric(tenant(), name(), spotDc(), dcList(), geoFabric);
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
    public Permissions getPermissions(final String user) throws C8DBException {
        return executor.execute(getPermissionsRequest(user), getPermissionsResponseDeserialzer());
    }

    @Override
    public <T> C8Cursor<T> query(final String query, final Map<String, Object> bindVars,
                                 final C8qlQueryOptions options, final Class<T> type) throws C8DBException {

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
        final CursorEntity result = executor.execute(queryNextRequest(cursorId, null), CursorEntity.class,
                hostHandle);
        return createCursor(result, type, null, hostHandle);
    }

    private <T> C8Cursor<T> createCursor(final CursorEntity result, final Class<T> type,
                                         final C8qlQueryOptions options, final HostHandle hostHandle) {

        final C8CursorExecute execute = new C8CursorExecute() {
            @Override
            public CursorEntity next(final String id, Map<String, String> meta) {
                return executor.execute(queryNextRequest(id, meta), CursorEntity.class, hostHandle);
            }

            @Override
            public void close(final String id, Map<String, String> meta) {
                executor.execute(queryCloseRequest(id, meta), Void.class, hostHandle);
            }
        };

        return cursorInitializer != null ? cursorInitializer.createInstance(this, execute, type, result)
                : new C8CursorImpl<T>(this, execute, type, result);
    }

    @Override
    public C8qlParseEntity parseQuery(final String query) throws C8DBException {
        return executor.execute(parseQueryRequest(query), C8qlParseEntity.class);
    }

    @Override
    public Collection<QueryEntity> getCurrentlyRunningQueries() throws C8DBException {
        return executor.execute(getCurrentlyRunningQueriesRequest(), new Type<Collection<QueryEntity>>() {
        }.getType());
    }

    @Override
    public C8qlExecutionExplainEntity explainQuery(final String query, final Map<String, Object> bindVars,
                                                   final C8qlQueryExplainOptions options) throws C8DBException {
        return executor.execute(explainQueryRequest(query, bindVars, options), C8qlExecutionExplainEntity.class);
    }

    @Override
    public void clearSlowQueries() throws C8DBException {
        executor.execute(clearSlowQueriesRequest(), Void.class);
    }

    @Override
    public Collection<QueryEntity> getSlowQueries() throws C8DBException {
        return executor.execute(getSlowQueriesRequest(), new Type<Collection<QueryEntity>>() {
        }.getType());
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
    public void killQuery(final String id) throws C8DBException {
        executor.execute(killQueryRequest(id), Void.class);
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
    public GraphEntity createGraph(final String name, final Collection<EdgeDefinition> edgeDefinitions,
                                   final GraphCreateOptions options) throws C8DBException {
        return executor.execute(createGraphRequest(name, edgeDefinitions, options), createGraphResponseDeserializer());
    }

    @Override
    public Collection<GraphEntity> getGraphs() throws C8DBException {
        return executor.execute(getGraphsRequest(), getGraphsResponseDeserializer());
    }

    @Override
    public <T> T transaction(final String action, final Class<T> type, final C8TransactionOptions options)
            throws C8DBException {
        return executor.execute(transactionRequest(action, options), transactionResponseDeserializer(type));
    }

    @Override
    public StreamTransactionEntity beginStreamTransaction(StreamTransactionOptions options) throws C8DBException {
        return executor.execute(beginStreamTransactionRequest(options), streamTransactionResponseDeserializer());
    }

    @Override
    public StreamTransactionEntity abortStreamTransaction(String id) throws C8DBException {
        return executor.execute(abortStreamTransactionRequest(id), streamTransactionResponseDeserializer());
    }

    @Override
    public StreamTransactionEntity getStreamTransaction(String id) throws C8DBException {
        return executor.execute(getStreamTransactionRequest(id), streamTransactionResponseDeserializer());
    }

    @Override
    public Collection<TransactionEntity> getStreamTransactions() throws C8DBException {
        return executor.execute(getStreamTransactionsRequest(), transactionsResponseDeserializer());
    }

    @Override
    public StreamTransactionEntity commitStreamTransaction(String id) throws C8DBException {
        return executor.execute(commitStreamTransactionRequest(id), streamTransactionResponseDeserializer());
    }

    @Override
    public DatabaseEntity getInfo() throws C8DBException {
        return executor.execute(getInfoRequest(), getInfoResponseDeserializer());
    }

    @Override
    public <V, E> TraversalEntity<V, E> executeTraversal(final Class<V> vertexClass, final Class<E> edgeClass,
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

    protected C8DatabaseImpl setCursorInitializer(final C8CursorInitializer cursorInitializer) {
        this.cursorInitializer = cursorInitializer;
        return this;
    }

    @Override
    public C8Stream stream(final String name) {
        return new C8StreamImpl(this, name);
    }

    @Override
    public void createPersistentStream(final String name, final C8StreamCreateOptions options)
            throws C8DBException {
        try {
            executor.execute(createC8PersistentStreamRequest(name, options), Void.class, null, Service.C8STREAMS);
        } catch (final C8DBException e) {
            if (!C8Errors.ERROR_STREAM_ALREADY_EXISTS.equals(e.getErrorNum())) {
                throw e;
            }
        }
    }

    @Override
    public Collection<C8StreamEntity> getPersistentStreams(final C8StreamCreateOptions options)
            throws C8DBException {
        return executor.execute(getC8PersistentStreamsRequest(options), getC8StreamsResponseDeserializer(), null, Service.C8STREAMS);
    }

    @Override
    public Collection<C8StreamEntity> getStreams() throws C8DBException {
        return executor.execute(getC8StreamsRequest(), getC8StreamsResponseDeserializer(), null, Service.C8STREAMS);
    }

    @Override
    public void clearBacklog() {
        executor.execute(clearC8StreamBacklogRequest(), Void.class, null, Service.C8STREAMS);
    }

    @Override
    public void clearBacklog(final String subscriptionName) {
        executor.execute(clearC8StreamBacklogRequest(subscriptionName), Void.class, null, Service.C8STREAMS);
    }

    @Override
    public void unsubscribe(final String subscriptionName) {
        executor.execute(unsubscribeRequest(subscriptionName), Void.class, null, Service.C8STREAMS);
    }

    @Override
    public Restql restql() {
        return new RestqlImpl(this);
    }

    @Override
    public UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition)
            throws C8DBException {
        return executor.execute(createUserQueryRequest(userQueryDefinition, null),
                createUserQueryResponseDeserializer());
    }

    @Override
    public UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition, final String user)
            throws C8DBException {
        return executor.execute(createUserQueryRequest(userQueryDefinition, user),
                createUserQueryResponseDeserializer());
    }

    @Override
    public <T> C8Cursor<T> executeUserQuery(final String userName, final String name, final Map<String, Object> bindVars, final Class<T> type)
            throws C8DBException {
        final Request request = userQueryRequest(userName, name, bindVars);
        final HostHandle hostHandle = new HostHandle();
        final CursorEntity result = executor.execute(request, CursorEntity.class, hostHandle);
        return createCursor(result, type, null, hostHandle);
    }

    @Override
    public C8Event event() {
        return new C8EventImpl(this);
    }

    @Override
    public C8Admin admin() {
        return new C8AdminImpl(this);
    }

    @Override 
    public C8ApiKeys apiKeys() {
        return new C8ApiKeysImpl(this);
    }

    @Override
    public C8Alerts alerts() {
        return new C8AlertsImpl(this);
    }
}
