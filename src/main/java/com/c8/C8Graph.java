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

import com.c8.entity.EdgeDefinition;
import com.c8.entity.GraphEntity;
import com.c8.model.GraphCreateOptions;

/**
 * Interface for operations on C8DB graph level.
 *
 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/">API Documentation</a>
 * @author Mark Vollmary
 */
public interface C8Graph extends C8SerializationAccessor {

	/**
	 * The the handler of the database the named graph is within
	 *
	 * @return database handler
	 */
	public C8Database db();

	/**
	 * The name of the collection
	 *
	 * @return collection name
	 */
	public String name();

	/**
	 * Checks whether the graph exists
	 *
	 * @return true if the graph exists, otherwise false
	 */
	boolean exists() throws C8DBException;

	/**
	 * Creates the graph in the graph module. The creation of a graph requires the name of the graph and a definition of
	 * its edges.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#create-a-graph">API
	 *      Documentation</a>
	 * @param edgeDefinitions
	 *            An array of definitions for the edge
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity create(Collection<EdgeDefinition> edgeDefinitions) throws C8DBException;

	/**
	 * Creates the graph in the graph module. The creation of a graph requires the name of the graph and a definition of
	 * its edges.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#create-a-graph">API
	 *      Documentation</a>
	 * @param edgeDefinitions
	 *            An array of definitions for the edge
	 * @param options
	 *            Additional options, can be null
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity create(Collection<EdgeDefinition> edgeDefinitions, GraphCreateOptions options) throws C8DBException;

	/**
	 * Deletes the graph from the database.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#drop-a-graph">API Documentation</a>
	 * @throws C8DBException
	 */
	void drop() throws C8DBException;

	/**
	 * Deletes the graph from the database.
	 *
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Gharial/Management.html#drop-a-graph">API
	 *      Documentation</a>
	 * @param dropCollections
	 *          Drop collections of this graph as well. Collections will only be
	 *          dropped if they are not used in other graphs.
	 * @throws C8DBException
	 */
	void drop(boolean dropCollections) throws C8DBException;

	/**
	 * Retrieves general information about the graph.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#get-a-graph">API Documentation</a>
	 * @return the definition content of this graph
	 * @throws C8DBException
	 */
	GraphEntity getInfo() throws C8DBException;

	/**
	 * Fetches all vertex collections from the graph and returns a list of collection names.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#list-vertex-collections">API
	 *      Documentation</a>
	 * @return all vertex collections within this graph
	 * @throws C8DBException
	 */
	Collection<String> getVertexCollections() throws C8DBException;

	/**
	 * Adds a vertex collection to the set of collections of the graph. If the collection does not exist, it will be
	 * created.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#add-vertex-collection">API
	 *      Documentation</a>
	 * @param name
	 *            Name of the vertex collection
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity addVertexCollection(String name) throws C8DBException;

	/**
	 * Returns a {@code C8VertexCollection} instance for the given vertex collection name.
	 *
	 * @param name
	 *            Name of the vertex collection
	 * @return collection handler
	 */
	C8VertexCollection vertexCollection(String name);

	/**
	 * Returns a {@code C8EdgeCollection} instance for the given edge collection name.
	 *
	 * @param name
	 *            Name of the edge collection
	 * @return collection handler
	 */
	C8EdgeCollection edgeCollection(String name);

	/**
	 * Fetches all edge collections from the graph and returns a list of collection names.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#list-edge-definitions">API
	 *      Documentation</a>
	 * @return all edge collections within this graph
	 * @throws C8DBException
	 */
	Collection<String> getEdgeDefinitions() throws C8DBException;

	/**
	 * Adds the given edge definition to the graph.
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#add-edge-definition">API
	 *      Documentation</a>
	 * @param definition
	 *            The edge definition
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity addEdgeDefinition(EdgeDefinition definition) throws C8DBException;

	/**
	 * Change one specific edge definition. This will modify all occurrences of this definition in all graphs known to
	 * your database
	 *
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#replace-an-edge-definition">API
	 *      Documentation</a>
	 * @param definition
	 *            The edge definition
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity replaceEdgeDefinition(EdgeDefinition definition) throws C8DBException;

	/**
	 * Remove one edge definition from the graph. This will only remove the edge collection, the vertex collections
	 * remain untouched and can still be used in your queries
	 *
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Gharial/Management.html#remove-an-edge-definition-from-the-graph">API
	 *      Documentation</a>
	 * @param definitionName
	 *            The name of the edge collection used in the definition
	 * @return information about the graph
	 * @throws C8DBException
	 */
	GraphEntity removeEdgeDefinition(String definitionName) throws C8DBException;

}
