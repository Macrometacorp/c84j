/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.C8DynamoBatchWriteItemEntity;
import com.c8db.entity.C8DynamoCreateTableEntity;
import com.c8db.entity.C8DynamoDeleteItemEntity;
import com.c8db.entity.C8DynamoGetItemEntity;
import com.c8db.entity.C8DynamoGetItemsEntity;
import com.c8db.entity.C8DynamoPutItemEntity;
import com.c8db.entity.C8DynamoDescribeTableEntity;
import com.c8db.entity.C8DynamoDeleteTableEntity;
import com.c8db.model.C8DynamoCreateTableOptions;
import com.c8db.model.C8DynamoGetItemsOptions;

import java.util.Collection;
import java.util.Map;

public interface C8Dynamo {

    /**
     * The handler of the database the collection is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a dynamo table with the given {@code options}
     *
     * @param options contains the request parameters
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoCreateTableEntity createTable(C8DynamoCreateTableOptions options) throws C8DBException;

    /**
     * This method deletes the dynamo table
     * @return C8DynamoDeleteEntity as response
     * @throws C8DBException
     */
    C8DynamoDeleteTableEntity deleteTable() throws C8DBException;

    /**
     * This method describes the schema of the dynamo table
     *
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoDescribeTableEntity describeTable() throws C8DBException;

    /**
     * This method inserts an item in the dynamo table
     * Note: it rewrites item if it exists
     *
     * @param value of the item
     * @return The result of putted item
     * @throws C8DBException
     */
    C8DynamoPutItemEntity putItem(Map<String, Object> value) throws C8DBException;

    /**
     * This method inserts batch of item in the dynamo table
     * Note: it doesn't rewrite item if it exists in the table.
     * Item will be returned in map `unprocessedItems` of the response
     *
     * @param values a batch of items that need to write
     * @return The result of written or unprocessed items
     * @throws C8DBException
     */
    C8DynamoBatchWriteItemEntity batchWriteItems(Collection<Map<String, Object>> values) throws C8DBException;

    /**
     * This method updates attributes in an existing item
     *
     * @param value of the item
     * @return The result of updated item
     * @throws C8DBException
     */
    C8DynamoPutItemEntity updateItem(Map<String, Object> value) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo table
     *
     * @param key of the item
     * @return the item
     * @throws C8DBException
     */
    C8DynamoGetItemEntity getItem(Map<String, Object> key) throws C8DBException;

    /**
     * This method deletes item in the dynamo table
     *
     * @param key of the item
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoDeleteItemEntity deleteItem(Map<String, Object> key) throws C8DBException;

    /**
     * This method returns list of items from the dynamo table
     *
     * @param options contains the request parameters
     * @return The Dynamo entity with response parameters
     * @throws C8DBException
     */
    C8DynamoGetItemsEntity getItems(C8DynamoGetItemsOptions options) throws C8DBException;

}
