/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.C8DynamoEntity;
import com.c8db.model.C8DynamoCreateOptions;

public interface C8Dynamo {

    /**
     * The handler of the database the collection is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a DynamoDb table with the given {@code options}
     *
     *
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoEntity create(C8DynamoCreateOptions options) throws C8DBException;

}
