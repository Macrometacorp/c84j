/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.*;
import com.c8db.model.C8DynamoCreateOptions;

import java.util.Collection;

public interface C8Dynamo {

    /**
     * The handler of the database the collection is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a DynamoDb table with the given {@code options}
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoEntity createTable(C8DynamoCreateOptions options) throws C8DBException;

    /**
     * This method deletes a dynomo table
     * @param options contains the request parameters
     * @return C8DynamoDeleteEntity as response
     * @throws C8DBException
     */
    C8DynamoDeleteEntity deleteTable(C8DynamoCreateOptions options) throws C8DBException;

    /**
     * This method describes the schema of the dynamo table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoDescribeEntity describeTable(C8DynamoCreateOptions options) throws C8DBException;

    /**
     * This method inserts an item in the dynamo like table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    //JSONObject putItem(JSONObject options) throws C8DBException;
    <T> MultiDocumentEntity<C8DynamoItemEntity> putItem(Collection<T> values) throws C8DBException;;

    /**
     * This method updates an existing item in the dynamo like table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<C8DynamoItemEntity> updateItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<C8DynamoItemEntity> getItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<C8DynamoItemEntity> deleteItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<C8DynamoItemEntity> getItems(Collection<T> values) throws C8DBException;
}
