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
 */

package com.c8db;

import java.util.Collection;
import java.util.Map;

import com.c8db.entity.C8DBVersion;
import com.c8db.entity.C8StreamEntity;
import com.c8db.entity.C8qlExecutionExplainEntity;
import com.c8db.entity.C8qlParseEntity;
import com.c8db.entity.CollectionEntity;
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

/**
 * Interface for operations on C8DB database level.
 *
 */
public interface C8Database extends C8SerializationAccessor {

    /**
     * Return the main entry point for the C8DB driver
     *
     * @return main entry point
     */
    C8DB c8db();

    /**
     * Returns the name of the tenant
     *
     * @return tenant name
     */
    String tenant();

    /**
     * Returns the name of the database
     *
     * @return database name
     */
    String name();

    /**
     * Returns the server name and version number.
     *
     * @return the server version, number
     * @throws C8DBException
     */
    C8DBVersion getVersion() throws C8DBException;

    /**
     * Checks whether the database exists
     *
     * @return true if the database exists, otherwise false
     */
    boolean exists() throws C8DBException;

    /**
     * Retrieves a list of all databases the current user can access
     *
     * @return a list of all databases the current user can access
     * @throws C8DBException
     */
    Collection<String> getAccessibleGeoFabrics() throws C8DBException;

    /**
     * Returns a {@code ArangoCollection} instance for the given collection name.
     *
     * @param name Name of the collection
     * @return collection handler
     */
    C8Collection collection(String name);

    /**
     * Creates a collection for the given collection's name, then returns collection
     * information from the server.
     *
     * @param name The name of the collection
     * @return information about the collection
     * @throws C8DBException
     */
    CollectionEntity createCollection(String name) throws C8DBException;

    /**
     * Creates a collection with the given {@code options} for this collection's
     * name, then returns collection information from the server.
     *
     * @param name    The name of the collection
     * @param options Additional options, can be null
     * @return information about the collection
     * @throws C8DBException
     */
    CollectionEntity createCollection(String name, CollectionCreateOptions options) throws C8DBException;

    /**
     * Fetches all collections from the database and returns an list of collection
     * descriptions.
     *
     * @return list of information about all collections
     * @throws C8DBException
     */
    Collection<CollectionEntity> getCollections() throws C8DBException;

    /**
     * Fetches all collections from the database and returns an list of collection
     * descriptions.
     *
     * @param options Additional options, can be null
     * @return list of information about all collections
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Getting.html#reads-all-collections">API
     *      Documentation</a>
     */
    Collection<CollectionEntity> getCollections(CollectionsReadOptions options) throws C8DBException;

    /**
     * Returns an index
     *
     * @param id The index-handle
     * @return information about the index
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Indexes/WorkingWith.html#read-index">API
     *      Documentation</a>
     */
    IndexEntity getIndex(String id) throws C8DBException;

    /**
     * Deletes an index
     *
     * @param id The index-handle
     * @return the id of the index
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Indexes/WorkingWith.html#delete-index">API
     *      Documentation</a>
     */
    String deleteIndex(String id) throws C8DBException;

    /**
     * Creates the database
     *
     * @return true if the database was created successfully.
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Database/DatabaseManagement.html#create-database">API
     *      Documentation</a>
     */
    Boolean create() throws C8DBException;

    /**
     * Deletes the database from the server.
     *
     * @return true if the database was dropped successfully
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Database/DatabaseManagement.html#drop-database">API
     *      Documentation</a>
     */
    Boolean drop() throws C8DBException;

    /**
     * Grants or revoke access to the database for user {@code user}. You need
     * permission to the _system database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
     *      API Documentation</a>
     */
    void grantAccess(String user, Permissions permissions) throws C8DBException;

    /**
     * Grants access to the database for user {@code user}. You need permission to
     * the _system database in order to execute this call.
     *
     * @param user The name of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
     *      API Documentation</a>
     */
    void grantAccess(String user) throws C8DBException;

    /**
     * Revokes access to the database dbname for user {@code user}. You need
     * permission to the _system database in order to execute this call.
     *
     * @param user The name of the user
     * @throws C8DBException
     */
    void revokeAccess(String user) throws C8DBException;

    /**
     * Clear the database access level, revert back to the default access level.
     *
     * @param user The name of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
     *      API Documentation</a>
     * @since ArangoDB 3.2.0
     */
    void resetAccess(String user) throws C8DBException;

    /**
     * Get specific database access level
     *
     * @param user The name of the user
     * @return permissions of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/#get-the-database-access-level">
     *      API Documentation</a>
     * @since ArangoDB 3.2.0
     */
    Permissions getPermissions(String user) throws C8DBException;

    /**
     * Performs a database query using the given {@code query} and {@code bindVars},
     * then returns a new {@code C8Cursor} instance for the result list.
     *
     * @param query    A C8QL query string
     * @param bindVars key/value pairs defining the variables to bind the query to
     * @param options  Additional options that will be passed to the query API, can
     *                 be null
     * @param type     The type of the result (POJO class, VPackSlice, String for
     *                 JSON, or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
     *      Documentation</a>
     */
    <T> C8Cursor<T> query(String query, Map<String, Object> bindVars, C8qlQueryOptions options, Class<T> type)
            throws C8DBException;

    /**
     * Performs a database query using the given {@code query}, then returns a new
     * {@code ArangoCursor} instance for the result list.
     *
     * @param query   An AQL query string
     * @param options Additional options that will be passed to the query API, can
     *                be null
     * @param type    The type of the result (POJO class, VPackSlice, String for
     *                JSON, or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
     *      Documentation</a>
     */
    <T> C8Cursor<T> query(String query, C8qlQueryOptions options, Class<T> type) throws C8DBException;

    /**
     * Performs a database query using the given {@code query} and {@code bindVars},
     * then returns a new {@code ArangoCursor} instance for the result list.
     *
     * @param query    An AQL query string
     * @param bindVars key/value pairs defining the variables to bind the query to
     * @param type     The type of the result (POJO class, VPackSlice, String for
     *                 JSON, or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
     *      Documentation</a>
     */
    <T> C8Cursor<T> query(String query, Map<String, Object> bindVars, Class<T> type) throws C8DBException;

    /**
     * Performs a database query using the given {@code query}, then returns a new
     * {@code ArangoCursor} instance for the result list.
     *
     * @param query An AQL query string
     * @param type  The type of the result (POJO class, VPackSlice, String for JSON,
     *              or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
     *      Documentation</a>
     */
    <T> C8Cursor<T> query(String query, Class<T> type) throws C8DBException;

    /**
     * Return an cursor from the given cursor-ID if still existing
     *
     * @param cursorId The ID of the cursor
     * @param type     The type of the result (POJO class, VPackSlice, String for
     *                 JSON, or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#read-next-batch-from-cursor">API
     *      Documentation</a>
     */
    <T> C8Cursor<T> cursor(String cursorId, Class<T> type) throws C8DBException;

    /**
     * Explain an AQL query and return information about it
     *
     * @param query    the query which you want explained
     * @param bindVars key/value pairs representing the bind parameters
     * @param options  Additional options, can be null
     * @return information about the query
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQuery/index.html#explain-an-aql-query">API
     *      Documentation</a>
     */
    C8qlExecutionExplainEntity explainQuery(String query, Map<String, Object> bindVars, C8qlQueryExplainOptions options)
            throws C8DBException;

    /**
     * Parse an AQL query and return information about it This method is for query
     * validation only. To actually query the database, see
     * {@link C8Database#query(String, Map, C8qlQueryOptions, Class)}
     *
     * @param query the query which you want parse
     * @return imformation about the query
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQuery/index.html#parse-an-aql-query">API
     *      Documentation</a>
     */
    C8qlParseEntity parseQuery(String query) throws C8DBException;

    /**
     * Returns a list of currently running AQL queries
     *
     * @return a list of currently running AQL queries
     * @throws C8DBException
     */
    Collection<QueryEntity> getCurrentlyRunningQueries() throws C8DBException;

    /**
     * Returns a list of slow running AQL queries
     *
     * @return a list of slow running AQL queries
     * @throws C8DBException
     */
    Collection<QueryEntity> getSlowQueries() throws C8DBException;

    /**
     * Returns the configuration for the AQL query tracking
     *
     * @return configuration for the AQL query tracking
     * @throws C8DBException
     */
    QueryTrackingPropertiesEntity getQueryTrackingProperties() throws C8DBException;

    /**
     * Changes the configuration for the AQL query tracking
     *
     * @param properties properties to be set
     * @return current set of properties
     * @throws C8DBException
     */
    QueryTrackingPropertiesEntity setQueryTrackingProperties(QueryTrackingPropertiesEntity properties)
            throws C8DBException;

    /**
     * Clears the list of slow AQL queries
     *
     * @throws C8DBException
     */
    void clearSlowQueries() throws C8DBException;

    /**
     * Kills a running query. The query will be terminated at the next cancelation
     * point.
     *
     * @param id The id of the query
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/AqlQuery/index.html#kills-a-running-aql-query">API
     *      Documentation</a>
     */
    void killQuery(String id) throws C8DBException;

    /**
     * Returns a {@code ArangoGraph} instance for the given graph name.
     *
     * @param name Name of the graph
     * @return graph handler
     */
    C8Graph graph(String name);

    /**
     * Create a new graph in the graph module. The creation of a graph requires the
     * name of the graph and a definition of its edges.
     *
     * @param name            Name of the graph
     * @param edgeDefinitions An array of definitions for the edge
     * @return information about the graph
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Management.html#create-a-graph">API
     *      Documentation</a>
     */
    GraphEntity createGraph(String name, Collection<EdgeDefinition> edgeDefinitions) throws C8DBException;

    /**
     * Create a new graph in the graph module. The creation of a graph requires the
     * name of the graph and a definition of its edges.
     *
     * @param name            Name of the graph
     * @param edgeDefinitions An array of definitions for the edge
     * @param options         Additional options, can be null
     * @return information about the graph
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Management.html#create-a-graph">API
     *      Documentation</a>
     */
    GraphEntity createGraph(String name, Collection<EdgeDefinition> edgeDefinitions, GraphCreateOptions options)
            throws C8DBException;

    /**
     * Lists all graphs known to the graph module
     *
     * @return graphs stored in this database
     * @throws C8DBException
     */
    Collection<GraphEntity> getGraphs() throws C8DBException;

    /**
     * Returns a {@code Restql} instance.
     *
     * @return Restql handler
     */
    Restql restql();

    /**
     * Performs a server-side transaction and returns its return value.
     *
     * @param action  A String evaluating to a JavaScript function to be executed on
     *                the server.
     * @param type    The type of the result (POJO class, VPackSlice or String for
     *                JSON)
     * @param options Additional options, can be null
     * @return the result of the transaction if it succeeded
     * @throws C8DBException
     */
    <T> T transaction(String action, Class<T> type, C8TransactionOptions options) throws C8DBException;

    /**
     * Begins a Stream Transaction.
     *
     * @param options Additional options, can be null
     * @return information about the transaction
     * @throws C8DBException
     */
    StreamTransactionEntity beginStreamTransaction(StreamTransactionOptions options) throws C8DBException;

    /**
     * Aborts a Stream Transaction.
     *
     * @return information about the transaction
     * @throws C8DBException
     */
    StreamTransactionEntity abortStreamTransaction(String id) throws C8DBException;

    /**
     * Gets information about a Stream Transaction.
     *
     * @return information about the transaction
     * @throws C8DBException
     */
    StreamTransactionEntity getStreamTransaction(String id) throws C8DBException;

    /**
     * Gets all the currently running Stream Transactions.
     *
     * @return all the currently running Stream Transactions
     * @throws C8DBException
     */
    Collection<TransactionEntity> getStreamTransactions() throws C8DBException;

    /**
     * Commits a Stream Transaction.
     *
     * @return information about the transaction
     * @throws C8DBException
     */
    StreamTransactionEntity commitStreamTransaction(String id) throws C8DBException;

    /**
     * Retrieves information about the current database
     *
     * @return information about the current database
     * @throws C8DBException
     */
    DatabaseEntity getInfo() throws C8DBException;

    /**
     * Execute a server-side traversal
     *
     * @param vertexClass The type of the vertex documents (POJO class, VPackSlice
     *                    or String for JSON)
     * @param edgeClass   The type of the edge documents (POJO class, VPackSlice or
     *                    String for JSON)
     * @param options     Additional options
     * @return Result of the executed traversal
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Traversal/index.html#executes-a-traversal">API
     *      Documentation</a>
     */
    <V, E> TraversalEntity<V, E> executeTraversal(Class<V> vertexClass, Class<E> edgeClass, TraversalOptions options)
            throws C8DBException;

    /**
     * Reads a single document
     *
     * @param id   The id of the document
     * @param type The type of the document (POJO class, VPackSlice or String for
     *             JSON)
     * @return the document identified by the id
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
     *      Documentation</a>
     */
    <T> T getDocument(String id, Class<T> type) throws C8DBException;

    /**
     * Reads a single document
     *
     * @param id      The id of the document
     * @param type    The type of the document (POJO class, VPackSlice or String for
     *                JSON)
     * @param options Additional options, can be null
     * @return the document identified by the id
     * @throws C8DBException
     */
    <T> T getDocument(String id, Class<T> type, DocumentReadOptions options) throws C8DBException;

    /**
     * Returns a {@code C8Stream} instance for the given stream name.
     *
     * @param name Name of the stream
     * @return stream handler
     */
    C8Stream stream(String name);

    /**
     * Create asynchronously a persistent stream for a given fabric.
     * 
     * @param name    of the stream
     * @param options C8StreamCreateOptions
     * @throws C8DBException
     */
    void createPersistentStream(final String name, final C8StreamCreateOptions options) throws C8DBException;

    /**
     * Get list of persistent streams under the given stream db. Returns either a
     * list of global or of local streams.
     * 
     * @param options
     * @return
     * @throws C8DBException
     */
    Collection<C8StreamEntity> getPersistentStreams(final C8StreamCreateOptions options) throws C8DBException;

    /**
     * Get list of all streams under given database.
     * 
     * @return
     * @throws C8DBException
     */
    Collection<C8StreamEntity> getStreams() throws C8DBException;

    /**
     * Clear backlog for all streams on a stream db.
     */
    void clearBacklog();

    /**
     * Clear backlog for given subscription.
     * 
     * @param subscriptionName Name of the subscription
     */
    void clearBacklog(final String subscriptionName);

    /**
     * Unsubscribes the given subscription on all streams on a stream db.
     * 
     * @param subscriptionName Identifying name of the subscripton.
     */
    void unsubscribe(final String subscriptionName);

    /**
     * Creates user query
     * 
     * @param userQueryDefinition
     * @return
     * @throws C8DBException
     */
    UserQueryEntity createUserQuery(UserQueryOptions userQueryDefinition) throws C8DBException;

    /**
     * Executes a saved using query using the given {@code name} and
     * {@code bindVars}, then returns a new {@code C8Cursor} instance for the result
     * list. If {@code userName} is null then tries to execute it for current user.
     *
     * @param userName user the query belongs to. If null executes it for current user
     * @param name     A user query name
     * @param bindVars key/value pairs defining the variables to bind the query to
     * @param type     The type of the result (POJO class, VPackSlice, String for
     *                 JSON, or Collection/List/Map)
     * @return cursor of the results
     * @throws C8DBException
     */
    <T> C8Cursor<T> executeUserQuery(final String userName, String name, Map<String, Object> bindVars, Class<T> type) throws C8DBException;

}
