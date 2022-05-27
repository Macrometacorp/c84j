/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */


package com.c8db.internal;

import com.c8db.C8Cursor;
import com.c8db.C8DBException;
import com.c8db.Restql;
import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;
import com.c8db.model.C8qlQueryOptions;

import java.util.Collection;
import java.util.Map;

public class RestqlImpl extends InternalRestql<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements Restql {

    protected RestqlImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public void drop(final String name) throws C8DBException {
        executor.execute(dropRequest(name), Void.class);
    }

    @Override
    public void drop(final String name, final String user) throws C8DBException {
        executor.execute(dropRequest(name, user), Void.class);
    }

    @Override
    public UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition) throws C8DBException {
        return db().createUserQuery(userQueryDefinition);
    }

    @Override
    public UserQueryEntity createUserQuery(final UserQueryOptions userQueryDefinition, final String user)
            throws C8DBException {
        return db().createUserQuery(userQueryDefinition, user);
    }

    @Override
    public <T> C8Cursor<T> executeUserQuery(final String name, final Map<String, Object> bindVars, Class<T> type) {
        return db().executeUserQuery(null, name, bindVars, type);
    }

    @Override
    public <T> C8Cursor<T> executeUserQueryByUserNameAndName(final String userName, final String name, final Map<String, Object> bindVars, Class<T> type) {
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
