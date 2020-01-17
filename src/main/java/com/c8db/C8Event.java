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

import com.c8db.entity.C8EventEntity;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.EventCreateOptions;

/**
 * Interface for operations on C8DB events level.
 *
 */
public interface C8Event extends C8SerializationAccessor {

    /**
     * The the handler of the database the collection with events is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a new document from the given document, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param value A representation of a single document (POJO, VPackSlice or
     *              String for JSON)
     * @return information about the document
     * @throws C8DBException
     */
    <T> C8EventEntity insertEvent(T value) throws C8DBException;

    /**
     * Creates a new document from the given document, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param value   A representation of a single document (POJO, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     */
    <T> C8EventEntity insertEvent(T value, EventCreateOptions options) throws C8DBException;

    /**
     * Retrieves the document with the given {@code key} from the collection.
     *
     * @param key  The key of the document
     * @param type The type of the document (POJO class, VPackSlice or String for
     *             JSON)
     * @return the document identified by the key
     * @throws C8DBException
     */
    C8EventEntity getEvent(String key) throws C8DBException;

    /**
     * Retrieves the document with the given {@code key} from the collection.
     *
     * @param key     The key of the document
     * @param type    The type of the document (POJO class, VPackSlice or String for
     *                JSON)
     * @param options Additional options, can be null
     * @return the document identified by the key
     * @throws C8DBException
     */
    C8EventEntity getEvent(String key, DocumentReadOptions options) throws C8DBException;

    /**
     * Retrieves multiple documents with the given {@code _key} from the collection.
     *
     * @param keys The keys of the documents
     * @param type The type of the documents (POJO class, VPackSlice or String for
     *             JSON)
     * @return the documents and possible errors
     * @throws C8DBException
     */
    Collection<C8EventEntity> getEvents(Collection<String> keys) throws C8DBException;

    /**
     * Deletes the document with the given {@code key} from the collection.
     *
     * @param key The key of the document
     * @throws C8DBException
     */
    void deleteEvent(String key) throws C8DBException;

    /**
     * Deletes multiple documents from the collection.
     *
     * @param values The keys of the documents or the documents themselves
     * @throws C8DBException
     */
    void deleteEvents(Collection<?> values) throws C8DBException;

}
