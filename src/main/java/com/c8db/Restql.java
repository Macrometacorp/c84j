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

import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for operations on restql level.
 */
public interface Restql extends C8SerializationAccessor {

    /**
     * The the handler of the database the user query is within
     *
     * @return database handler
     */
    C8Database db();

    /**
     * Deletes user query from the database.
     *
     * @throws C8DBException
     * @see <a href=
     * "https://docs.arangodb.com/current/HTTP/Gharial/Management.html#drop-a-graph">API
     * Documentation</a>
     */
    void drop(final String name) throws C8DBException;

    /**
     * Deletes user query from the database.
     *
     * @throws C8DBException
     * @see <a href=
     * "https://docs.arangodb.com/current/HTTP/Gharial/Management.html#drop-a-graph">API
     * Documentation</a>
     */
    void drop(final String name, final String user) throws C8DBException;

    /**
     * Saves a query for a user for a given fabric.
     *
     * @param userQueryDefinition user query options
     * @return user query entity.
     * @throws C8DBException
     */
    UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition) throws C8DBException;

    /**
     * Saves a query for a user for a given fabric and a given user.
     *
     * @param userQueryDefinition user query options
     * @param user                username
     * @return user query entity.
     * @throws C8DBException
     */
    UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition, final String user) throws C8DBException;

    /**
     * Executes saved query by name
     *
     * @param name     name of the saved query
     * @param bindVars vars for the query
     * @param type     result type
     * @return
     */
    <T> C8Cursor<T> executeUserQuery(String name, Map<String, Object> bindVars, Class<T> type);

    /**
     * Executes saved query by name for the give user
     *
     * @param <T>
     * @param userName user name
     * @param name     query name
     * @param bindVars bind vars
     * @param type     return type
     * @return
     */
    <T> C8Cursor<T> executeUserQueryByUserNameAndName(String userName, String name, Map<String, Object> bindVars,
                                                      Class<T> type);

    /**
     * Fetches all user queries associated with the current user
     *
     * @return all user queries for current user
     * @throws C8DBException
     */
    Collection<UserQueryEntity> getUserQueries() throws C8DBException;

    /**
     * Fetches all user queries associated with given user
     *
     * @return all user queries for given user
     * @throws C8DBException
     * @userName userName
     */
    Collection<UserQueryEntity> getUserQueries(final String userName) throws C8DBException;

}
