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

import com.c8db.entity.EdgeEntity;
import com.c8db.entity.EdgeUpdateEntity;
import com.c8db.model.EdgeCreateOptions;
import com.c8db.model.EdgeDeleteOptions;
import com.c8db.model.EdgeReplaceOptions;
import com.c8db.model.EdgeUpdateOptions;
import com.c8db.model.GraphDocumentReadOptions;

/**
 * Interface for operations on C8DB edge collection level.
 * 
 */
public interface C8EdgeCollection extends C8SerializationAccessor {

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
     * Creates a new edge in the collection
     * 
     * @param value A representation of a single edge (POJO, VPackSlice or String
     *              for JSON)
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeEntity insertEdge(T value) throws C8DBException;

    /**
     * Creates a new edge in the collection
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#create-an-edge">API
     *      Documentation</a>
     * @param value   A representation of a single edge (POJO, VPackSlice or String
     *                for JSON)
     * @param options Additional options, can be null
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeEntity insertEdge(T value, EdgeCreateOptions options) throws C8DBException;

    /**
     * Fetches an existing edge
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#get-an-edge">API
     *      Documentation</a>
     * @param key  The key of the edge
     * @param type The type of the edge-document (POJO class, VPackSlice or String
     *             for JSON)
     * @return the edge identified by the key
     * @throws C8DBException
     */
    <T> T getEdge(String key, Class<T> type) throws C8DBException;

    /**
     * Fetches an existing edge
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#get-an-edge">API
     *      Documentation</a>
     * @param key     The key of the edge
     * @param type    The type of the edge-document (POJO class, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return the edge identified by the key
     * @throws C8DBException
     */
    <T> T getEdge(String key, Class<T> type, GraphDocumentReadOptions options) throws C8DBException;

    /**
     * Replaces the edge with key with the one in the body, provided there is such a
     * edge and no precondition is violated
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#replace-an-edge">API
     *      Documentation</a>
     * @param key The key of the edge
     * @param <T> The type of the edge-document (POJO class, VPackSlice or String
     *            for JSON)
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeUpdateEntity replaceEdge(String key, T value) throws C8DBException;

    /**
     * Replaces the edge with key with the one in the body, provided there is such a
     * edge and no precondition is violated
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#replace-an-edge">API
     *      Documentation</a>
     * @param key     The key of the edge
     * @param <T>     The type of the edge-document (POJO class, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeUpdateEntity replaceEdge(String key, T value, EdgeReplaceOptions options) throws C8DBException;

    /**
     * Partially updates the edge identified by document-key. The value must contain
     * a document with the attributes to patch (the patch document). All attributes
     * from the patch document will be added to the existing document if they do not
     * yet exist, and overwritten in the existing document if they do exist there.
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#modify-an-edge">API
     *      Documentation</a>
     * @param key The key of the edge
     * @param <T> The type of the edge-document (POJO class, VPackSlice or String
     *            for JSON)
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeUpdateEntity updateEdge(String key, T value) throws C8DBException;

    /**
     * Partially updates the edge identified by document-key. The value must contain
     * a document with the attributes to patch (the patch document). All attributes
     * from the patch document will be added to the existing document if they do not
     * yet exist, and overwritten in the existing document if they do exist there.
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#modify-an-edge">API
     *      Documentation</a>
     * @param key     The key of the edge
     * @param <T>     The type of the edge-document (POJO class, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the edge
     * @throws C8DBException
     */
    <T> EdgeUpdateEntity updateEdge(String key, T value, EdgeUpdateOptions options) throws C8DBException;

    /**
     * Removes a edge
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#remove-an-edge">API
     *      Documentation</a>
     * @param key The key of the edge
     * @throws C8DBException
     */
    void deleteEdge(String key) throws C8DBException;

    /**
     * Removes a edge
     * 
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Gharial/Edges.html#remove-an-edge">API
     *      Documentation</a>
     * @param key     The key of the edge
     * @param options Additional options, can be null
     * @throws C8DBException
     */
    void deleteEdge(String key, EdgeDeleteOptions options) throws C8DBException;

}
