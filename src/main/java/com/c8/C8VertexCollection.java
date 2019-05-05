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

import com.c8.entity.VertexEntity;
import com.c8.entity.VertexUpdateEntity;
import com.c8.model.DocumentReadOptions;
import com.c8.model.VertexCreateOptions;
import com.c8.model.VertexDeleteOptions;
import com.c8.model.VertexReplaceOptions;
import com.c8.model.VertexUpdateOptions;

/**
 * Interface for operations on C8DB vertex collection level.
 * 
 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html">API Documentation</a>
 * @author Mark Vollmary
 */
public interface C8VertexCollection extends C8SerializationAccessor {

	/**
	 * The the handler of the named graph the edge collection is within
	 * 
	 * @return graph handler
	 */
	C8Graph graph();

	/**
	 * The name of the edge collection
	 * 
	 * @return collection name
	 */
	String name();

	/**
	 * Removes a vertex collection from the graph and optionally deletes the collection, if it is not used in any other
	 * graph
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Management.html#remove-vertex-collection">API
	 *      Documentation</a>
	 * @throws C8DBException
	 */
	void drop() throws C8DBException;

	/**
	 * Creates a new vertex in the collection
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#create-a-vertex">API Documentation</a>
	 * @param value
	 *            A representation of a single vertex (POJO, VPackSlice or String for JSON)
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexEntity insertVertex(T value) throws C8DBException;

	/**
	 * Creates a new vertex in the collection
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#create-a-vertex">API Documentation</a>
	 * @param value
	 *            A representation of a single vertex (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexEntity insertVertex(T value, VertexCreateOptions options) throws C8DBException;

	/**
	 * Retrieves the vertex document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#get-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param type
	 *            The type of the vertex-document (POJO class, VPackSlice or String for JSON)
	 * @return the vertex identified by the key
	 * @throws C8DBException
	 */
	<T> T getVertex(String key, Class<T> type) throws C8DBException;

	/**
	 * Retrieves the vertex document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#get-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param type
	 *            The type of the vertex-document (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return the vertex identified by the key
	 * @throws C8DBException
	 */
	<T> T getVertex(String key, Class<T> type, DocumentReadOptions options) throws C8DBException;

	/**
	 * Replaces the vertex with key with the one in the body, provided there is such a vertex and no precondition is
	 * violated
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#replace-a-vertex">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param value
	 *            A representation of a single vertex (POJO, VPackSlice or String for JSON)
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexUpdateEntity replaceVertex(String key, T value) throws C8DBException;

	/**
	 * Replaces the vertex with key with the one in the body, provided there is such a vertex and no precondition is
	 * violated
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#replace-a-vertex">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param value
	 *            A representation of a single vertex (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexUpdateEntity replaceVertex(String key, T value, VertexReplaceOptions options) throws C8DBException;

	/**
	 * Partially updates the vertex identified by document-key. The value must contain a document with the attributes to
	 * patch (the patch document). All attributes from the patch document will be added to the existing document if they
	 * do not yet exist, and overwritten in the existing document if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#modify-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param type
	 *            The type of the vertex-document (POJO class, VPackSlice or String for JSON)
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexUpdateEntity updateVertex(String key, T value) throws C8DBException;

	/**
	 * Partially updates the vertex identified by document-key. The value must contain a document with the attributes to
	 * patch (the patch document). All attributes from the patch document will be added to the existing document if they
	 * do not yet exist, and overwritten in the existing document if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#modify-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param type
	 *            The type of the vertex-document (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the vertex
	 * @throws C8DBException
	 */
	<T> VertexUpdateEntity updateVertex(String key, T value, VertexUpdateOptions options) throws C8DBException;

	/**
	 * Deletes the vertex with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#remove-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @throws C8DBException
	 */
	void deleteVertex(String key) throws C8DBException;

	/**
	 * Deletes the vertex with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Gharial/Vertices.html#remove-a-vertex">API Documentation</a>
	 * @param key
	 *            The key of the vertex
	 * @param options
	 *            Additional options, can be null
	 * @throws C8DBException
	 */
	void deleteVertex(String key, VertexDeleteOptions options) throws C8DBException;

}
