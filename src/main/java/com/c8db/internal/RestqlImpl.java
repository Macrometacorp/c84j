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

package com.c8db.internal;

import java.util.Collection;
import java.util.Map;

import com.c8db.C8Cursor;
import com.c8db.C8DBException;
import com.c8db.Restql;
import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;

/**
 *
 */
public class RestqlImpl extends InternalRestql<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements Restql {

    protected RestqlImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public void drop(String name) throws C8DBException {
        executor.execute(dropRequest(null), Void.class);
    }
    
    @Override
    public UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition) throws C8DBException {
        return db().createUserQuery(userQueryDefinition);
    }

    @Override
    public <T> C8Cursor<T> executeUserQuery (final String name, final Map<String, Object> bindVars, Class<T> type) {
        return db().executeUserQuery(null, name, bindVars, type);
    }

    @Override
    public <T> C8Cursor<T> executeUserQueryByUserNameAndName (final String userName, final String name, final Map<String, Object> bindVars, Class<T> type) {
        return db().executeUserQuery(userName, name, bindVars, type);
    }
    
    @Override
    public Collection<UserQueryEntity> getUserQueries() throws C8DBException {
        return executor.execute(getUserQueriesRequest(), getUserQueriesResponseDeserializer());
    }

    @Override
    public Collection<UserQueryEntity> getUserQueries(final String userName) throws C8DBException {
        return executor.execute(getUserQueriesRequest(userName), getUserQueriesResponseDeserializer());
    }
}