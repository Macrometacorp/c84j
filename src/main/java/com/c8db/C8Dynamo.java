/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.DynamoEntity;
import com.c8db.model.C8KVCreateOptions;

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
     * @param options Additional options, can be null
     * @return The Dynamo entity
     * @throws C8DBException
     */
    DynamoEntity create(C8KVCreateOptions options) throws C8DBException;

}
