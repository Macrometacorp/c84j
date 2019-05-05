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

package com.c8;

import java.util.Collection;
import java.util.Map;

import com.c8.entity.C8qlExecutionExplainEntity;
import com.c8.entity.C8qlFunctionEntity;
import com.c8.entity.C8qlParseEntity;
import com.c8.entity.C8DBVersion;
import com.c8.entity.CollectionEntity;
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

/**
 * Interface for operations on C8DB database level.
 * 
 * @see <a href="https://docs.c8db.com/current/HTTP/Database/">Databases API Documentation</a>
 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQuery/">Query API Documentation</a>
 * @author Mark Vollmary
 */
public interface C8Database extends C8SerializationAccessor {

	/**
	 * Return the main entry point for the C8DB driver
	 * 
	 * @return main entry point
	 */
	C8DB c8();

	/**
	 * Returns the name of the database
	 * 
	 * @return database name
	 */
	String name();

	/**
	 * Returns the server name and version number.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/MiscellaneousFunctions/index.html#return-server-version">API
	 *      Documentation</a>
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
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Database/DatabaseManagement.html#list-of-accessible-databases">API
	 *      Documentation</a>
	 * @return a list of all databases the current user can access
	 * @throws C8DBException
	 */
	Collection<String> getAccessibleDatabases() throws C8DBException;

	/**
	 * Returns a {@code C8Collection} instance for the given collection name.
	 * 
	 * @param name
	 *            Name of the collection
	 * @return collection handler
	 */
	C8Collection collection(String name);

	/**
	 * Creates a collection for the given collection's name, then returns collection information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#create-collection">API
	 *      Documentation</a>
	 * @param name
	 *            The name of the collection
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity createCollection(String name) throws C8DBException;

	/**
	 * Creates a collection with the given {@code options} for this collection's name, then returns collection
	 * information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#create-collection">API
	 *      Documentation</a>
	 * @param name
	 *            The name of the collection
	 * @param options
	 *            Additional options, can be null
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity createCollection(String name, CollectionCreateOptions options) throws C8DBException;

	/**
	 * Fetches all collections from the database and returns an list of collection descriptions.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Getting.html#reads-all-collections">API
	 *      Documentation</a>
	 * @return list of information about all collections
	 * @throws C8DBException
	 */
	Collection<CollectionEntity> getCollections() throws C8DBException;

	/**
	 * Fetches all collections from the database and returns an list of collection descriptions.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Getting.html#reads-all-collections">API
	 *      Documentation</a>
	 * @param options
	 *            Additional options, can be null
	 * @return list of information about all collections
	 * @throws C8DBException
	 */
	Collection<CollectionEntity> getCollections(CollectionsReadOptions options) throws C8DBException;

	/**
	 * Returns an index
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/WorkingWith.html#read-index">API Documentation</a>
	 * @param id
	 *            The index-handle
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity getIndex(String id) throws C8DBException;

	/**
	 * Deletes an index
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/WorkingWith.html#delete-index">API Documentation</a>
	 * @param id
	 *            The index-handle
	 * @return the id of the index
	 * @throws C8DBException
	 */
	String deleteIndex(String id) throws C8DBException;

	/**
	 * Creates the database
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Database/DatabaseManagement.html#create-database">API
	 *      Documentation</a>
	 * @return true if the database was created successfully.
	 * @throws C8DBException
	 */
	Boolean create() throws C8DBException;

	/**
	 * Deletes the database from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Database/DatabaseManagement.html#drop-database">API
	 *      Documentation</a>
	 * @return true if the database was dropped successfully
	 * @throws C8DBException
	 */
	Boolean drop() throws C8DBException;

	/**
	 * Grants or revoke access to the database for user {@code user}. You need permission to the _system database in
	 * order to execute this call.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
	 *      API Documentation</a>
	 * @param user
	 *            The name of the user
	 * @param permissions
	 *            The permissions the user grant
	 * @throws C8DBException
	 */
	void grantAccess(String user, Permissions permissions) throws C8DBException;

	/**
	 * Grants access to the database for user {@code user}. You need permission to the _system database in order to
	 * execute this call.
	 *
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
	 *      API Documentation</a>
	 * @param user
	 *            The name of the user
	 * @throws C8DBException
	 */
	void grantAccess(String user) throws C8DBException;

	/**
	 * Revokes access to the database dbname for user {@code user}. You need permission to the _system database in order
	 * to execute this call.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
	 *      API Documentation</a>
	 * @param user
	 *            The name of the user
	 * @throws C8DBException
	 */
	void revokeAccess(String user) throws C8DBException;

	/**
	 * Clear the database access level, revert back to the default access level.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-database-access">
	 *      API Documentation</a>
	 * @param user
	 *            The name of the user
	 * @since C8DB 3.2.0
	 * @throws C8DBException
	 */
	void resetAccess(String user) throws C8DBException;

	/**
	 * Sets the default access level for collections within this database for the user {@code user}. You need permission
	 * to the _system database in order to execute this call.
	 * 
	 * @param user
	 *            The name of the user
	 * @param permissions
	 *            The permissions the user grant
	 * @since C8DB 3.2.0
	 * @throws C8DBException
	 */
	void grantDefaultCollectionAccess(String user, Permissions permissions) throws C8DBException;

	/**
	 * Get specific database access level
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/#get-the-database-access-level"> API
	 *      Documentation</a>
	 * @param user
	 *            The name of the user
	 * @return permissions of the user
	 * @since C8DB 3.2.0
	 * @throws C8DBException
	 */
	Permissions getPermissions(String user) throws C8DBException;

	/**
	 * Performs a database query using the given {@code query} and {@code bindVars}, then returns a new
	 * {@code C8Cursor} instance for the result list.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
	 *      Documentation</a>
	 * @param query
	 *            An AQL query string
	 * @param bindVars
	 *            key/value pairs defining the variables to bind the query to
	 * @param options
	 *            Additional options that will be passed to the query API, can be null
	 * @param type
	 *            The type of the result (POJO class, VPackSlice, String for JSON, or Collection/List/Map)
	 * @return cursor of the results
	 * @throws C8DBException
	 */
	<T> C8Cursor<T> query(String query, Map<String, Object> bindVars, C8qlQueryOptions options, Class<T> type)
			throws C8DBException;

	/**
	 * Performs a database query using the given {@code query}, then returns a new {@code C8Cursor} instance for the
	 * result list.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
	 *      Documentation</a>
	 * @param query
	 *            An AQL query string
	 * @param options
	 *            Additional options that will be passed to the query API, can be null
	 * @param type
	 *            The type of the result (POJO class, VPackSlice, String for JSON, or Collection/List/Map)
	 * @return cursor of the results
	 * @throws C8DBException
	 */
	<T> C8Cursor<T> query(String query, C8qlQueryOptions options, Class<T> type) throws C8DBException;

	/**
	 * Performs a database query using the given {@code query} and {@code bindVars}, then returns a new
	 * {@code C8Cursor} instance for the result list.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
	 *      Documentation</a>
	 * @param query
	 *            An AQL query string
	 * @param bindVars
	 *            key/value pairs defining the variables to bind the query to
	 * @param type
	 *            The type of the result (POJO class, VPackSlice, String for JSON, or Collection/List/Map)
	 * @return cursor of the results
	 * @throws C8DBException
	 */
	<T> C8Cursor<T> query(String query, Map<String, Object> bindVars, Class<T> type) throws C8DBException;

	/**
	 * Performs a database query using the given {@code query}, then returns a new {@code C8Cursor} instance for the
	 * result list.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#create-cursor">API
	 *      Documentation</a>
	 * @param query
	 *            An AQL query string
	 * @param type
	 *            The type of the result (POJO class, VPackSlice, String for JSON, or Collection/List/Map)
	 * @return cursor of the results
	 * @throws C8DBException
	 */
	<T> C8Cursor<T> query(String query, Class<T> type) throws C8DBException;

	/**
	 * Return an cursor from the given cursor-ID if still existing
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQueryCursor/AccessingCursors.html#read-next-batch-from-cursor">API
	 *      Documentation</a>
	 * @param cursorId
	 *            The ID of the cursor
	 * @param type
	 *            The type of the result (POJO class, VPackSlice, String for JSON, or Collection/List/Map)
	 * @return cursor of the results
	 * @throws C8DBException
	 */
	<T> C8Cursor<T> cursor(String cursorId, Class<T> type) throws C8DBException;

	/**
	 * Explain an AQL query and return information about it
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQuery/index.html#explain-an-aql-query">API
	 *      Documentation</a>
	 * @param query
	 *            the query which you want explained
	 * @param bindVars
	 *            key/value pairs representing the bind parameters
	 * @param options
	 *            Additional options, can be null
	 * @return information about the query
	 * @throws C8DBException
	 */
	C8qlExecutionExplainEntity explainQuery(String query, Map<String, Object> bindVars, C8qlQueryExplainOptions options)
			throws C8DBException;

	/**
	 * Parse an AQL query and return information about it This method is for query validation only. To actually query
	 * the database, see {@link C8Database#query(String, Map, C8qlQueryOptions, Class)}
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlQuery/index.html#parse-an-aql-query">API
	 *      Documentation</a>
	 * @param query
	 *            the query which you want parse
	 * @return imformation about the query
	 * @throws C8DBException
	 */
	C8qlParseEntity parseQuery(String query) throws C8DBException;

	/**
	 * Clears the AQL query cache
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQueryCache/index.html#clears-any-results-in-the-aql-query-cache">API
	 *      Documentation</a>
	 * @throws C8DBException
	 */
	void clearQueryCache() throws C8DBException;

	/**
	 * Returns the global configuration for the AQL query cache
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQueryCache/index.html#returns-the-global-properties-for-the-aql-query-cache">API
	 *      Documentation</a>
	 * @return configuration for the AQL query cache
	 * @throws C8DBException
	 */
	QueryCachePropertiesEntity getQueryCacheProperties() throws C8DBException;

	/**
	 * Changes the configuration for the AQL query cache. Note: changing the properties may invalidate all results in
	 * the cache.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQueryCache/index.html#globally-adjusts-the-aql-query-result-cache-properties">API
	 *      Documentation</a>
	 * @param properties
	 *            properties to be set
	 * @return current set of properties
	 * @throws C8DBException
	 */
	QueryCachePropertiesEntity setQueryCacheProperties(QueryCachePropertiesEntity properties) throws C8DBException;

	/**
	 * Returns the configuration for the AQL query tracking
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#returns-the-properties-for-the-aql-query-tracking">API
	 *      Documentation</a>
	 * @return configuration for the AQL query tracking
	 * @throws C8DBException
	 */
	QueryTrackingPropertiesEntity getQueryTrackingProperties() throws C8DBException;

	/**
	 * Changes the configuration for the AQL query tracking
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#changes-the-properties-for-the-aql-query-tracking">API
	 *      Documentation</a>
	 * @param properties
	 *            properties to be set
	 * @return current set of properties
	 * @throws C8DBException
	 */
	QueryTrackingPropertiesEntity setQueryTrackingProperties(QueryTrackingPropertiesEntity properties)
			throws C8DBException;

	/**
	 * Returns a list of currently running AQL queries
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#returns-the-currently-running-aql-queries">API
	 *      Documentation</a>
	 * @return a list of currently running AQL queries
	 * @throws C8DBException
	 */
	Collection<QueryEntity> getCurrentlyRunningQueries() throws C8DBException;

	/**
	 * Returns a list of slow running AQL queries
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#returns-the-list-of-slow-aql-queries">API
	 *      Documentation</a>
	 * @return a list of slow running AQL queries
	 * @throws C8DBException
	 */
	Collection<QueryEntity> getSlowQueries() throws C8DBException;

	/**
	 * Clears the list of slow AQL queries
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#clears-the-list-of-slow-aql-queries">API
	 *      Documentation</a>
	 * @throws C8DBException
	 */
	void clearSlowQueries() throws C8DBException;

	/**
	 * Kills a running query. The query will be terminated at the next cancelation point.
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/AqlQuery/index.html#kills-a-running-aql-query">API
	 *      Documentation</a>
	 * @param id
	 *            The id of the query
	 * @throws C8DBException
	 */
	void killQuery(String id) throws C8DBException;

	/**
	 * Create a new AQL user function
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/AqlUserFunctions/index.html#create-aql-user-function">API
	 *      Documentation</a>
	 * @param name
	 *            A valid AQL function name, e.g.: `"myfuncs::accounting::calculate_vat"`
	 * @param code
	 *            A String evaluating to a JavaScript function
	 * @param options
	 *            Additional options, can be null
	 * @throws C8DBException
	 */
	void createAqlFunction(String name, String code, C8qlFunctionCreateOptions options) throws C8DBException;

	/**
	 * Deletes the AQL user function with the given name from the database.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlUserFunctions/index.html#remove-existing-aql-user-function">API
	 *      Documentation</a>
	 * @param name
	 *            The name of the user function to delete
	 * @param options
	 *            Additional options, can be null
	 * @return number of deleted functions (since C8DB 3.4.0)
	 * @throws C8DBException
	 */
	Integer deleteAqlFunction(String name, C8qlFunctionDeleteOptions options) throws C8DBException;

	/**
	 * Gets all reqistered AQL user functions
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AqlUserFunctions/index.html#return-registered-aql-user-functions">API
	 *      Documentation</a>
	 * @param options
	 *            Additional options, can be null
	 * @return all reqistered AQL user functions
	 * @throws C8DBException
	 */
	Collection<C8qlFunctionEntity> getAqlFunctions(C8qlFunctionGetOptions options) throws C8DBException;

	/**
	 * Returns a {@code C8Graph} instance for the given graph name.
	 * 
	 * @param name
	 *            Name of the graph
	 * @return graph handler
	 */
	C8Graph graph(String name);

	/**
	 * Create a new graph in the graph module. The creation of a graph requires the name of the graph and a definition
	 * of its edges.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#create-a-graph">API
	 *      Documentation</a>
	 * @param name
	 *            Name of the graph
	 * @param edgeDefinitions
	 *            An array of definitions for the edge
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity createGraph(String name, Collection<EdgeDefinition> edgeDefinitions) throws C8DBException;

	/**
	 * Create a new graph in the graph module. The creation of a graph requires the name of the graph and a definition
	 * of its edges.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#create-a-graph">API
	 *      Documentation</a>
	 * @param name
	 *            Name of the graph
	 * @param edgeDefinitions
	 *            An array of definitions for the edge
	 * @param options
	 *            Additional options, can be null
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity createGraph(String name, Collection<EdgeDefinition> edgeDefinitions, GraphCreateOptions options)
			throws C8DBException;

	/**
	 * Lists all graphs known to the graph module
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#list-all-graphs">API
	 *      Documentation</a>
	 * @return graphs stored in this database
	 * @throws C8DBException
	 */
	Collection<GraphEntity> getGraphs() throws C8DBException;

	/**
	 * Performs a server-side transaction and returns its return value.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Transaction/index.html#execute-transaction">API
	 *      Documentation</a>
	 * @param action
	 *            A String evaluating to a JavaScript function to be executed on the server.
	 * @param type
	 *            The type of the result (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return the result of the transaction if it succeeded
	 * @throws C8DBException
	 */
	<T> T transaction(String action, Class<T> type, TransactionOptions options) throws C8DBException;

	/**
	 * Retrieves information about the current database
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Database/DatabaseManagement.html#information-of-the-database">API
	 *      Documentation</a>
	 * @return information about the current database
	 * @throws C8DBException
	 */
	DatabaseEntity getInfo() throws C8DBException;

	/**
	 * Execute a server-side traversal
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/Traversal/index.html#executes-a-traversal">API
	 *      Documentation</a>
	 * @param vertexClass
	 *            The type of the vertex documents (POJO class, VPackSlice or String for JSON)
	 * @param edgeClass
	 *            The type of the edge documents (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options
	 * @return Result of the executed traversal
	 * @throws C8DBException
	 */
	<V, E> TraversalEntity<V, E> executeTraversal(Class<V> vertexClass, Class<E> edgeClass, TraversalOptions options)
			throws C8DBException;

	/**
	 * Reads a single document
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
	 *      Documentation</a>
	 * @param id
	 *            The id of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON)
	 * @return the document identified by the id
	 * @throws C8DBException
	 */
	<T> T getDocument(String id, Class<T> type) throws C8DBException;

	/**
	 * Reads a single document
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
	 *      Documentation</a>
	 * @param id
	 *            The id of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return the document identified by the id
	 * @throws C8DBException
	 */
	<T> T getDocument(String id, Class<T> type, DocumentReadOptions options) throws C8DBException;

	/**
	 * Reload the routing table.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/AdministrationAndMonitoring/index.html#reloads-the-routing-information">API
	 *      Documentation</a>
	 * @throws C8DBException
	 */
	void reloadRouting() throws C8DBException;

	/**
	 * Returns a new {@link C8Route} instance for the given path (relative to the database) that can be used to
	 * perform arbitrary requests.
	 * 
	 * @param path
	 *            The database-relative URL of the route
	 * @return {@link C8Route}
	 */
	C8Route route(String... path);

	/**
	 * Fetches all views from the database and returns an list of view descriptions.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Views/Getting.html#reads-all-views">API Documentation</a>
	 * @return list of information about all views
	 * @throws C8DBException
	 * @since C8DB 3.4.0
	 */
	Collection<ViewEntity> getViews() throws C8DBException;

	/**
	 * Returns a {@code C8View} instance for the given view name.
	 * 
	 * @param name
	 *            Name of the view
	 * @return view handler
	 * @since C8DB 3.4.0
	 */
	C8View view(String name);

	/**
	 * Returns a {@code C8Search} instance for the given C8Search view name.
	 * 
	 * @param name
	 *            Name of the view
	 * @return C8Search view handler
	 * @since C8DB 3.4.0
	 */
	C8Search c8Search(String name);

	/**
	 * Creates a view of the given {@code type}, then returns view information from the server.
	 * 
	 * @param name
	 *            The name of the view
	 * @param type
	 *            The type of the view
	 * @return information about the view
	 * @since C8DB 3.4.0
	 * @throws C8DBException
	 */
	ViewEntity createView(String name, ViewType type) throws C8DBException;

	/**
	 * Creates a C8Search view with the given {@code options}, then returns view information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Views/C8Search.html#create-c8search-view">API
	 *      Documentation</a>
	 * @param name
	 *            The name of the view
	 * @param options
	 *            Additional options, can be null
	 * @return information about the view
	 * @since C8DB 3.4.0
	 * @throws C8DBException
	 */
	ViewEntity createC8Search(String name, C8SearchCreateOptions options) throws C8DBException;

}
