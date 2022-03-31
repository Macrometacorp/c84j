/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.entity.C8DynamoEntity;
import com.c8db.entity.C8DynamoItemEntity;
import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.C8DynamoDescribeEntity;
import com.c8db.entity.C8DynamoDeleteEntity;
import com.c8db.model.C8DynamoCreateOptions;

import java.util.Collection;
import java.util.List;

public interface C8Dynamo {

    /**
     * The handler of the database the collection is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a DynamoDb table with the given {@code options}
     * @param tableName The name of Dynamo like collection
     * @param attributeDefinitionList List containing schema
     * @param keySchema The schema for the primary and composite key
     * @return The Dynamo entity
     * @throws C8DBException
     */
    C8DynamoEntity createTable(String tableName, List<DynamoAttributeDefinition> attributeDefinitionList,
                               List<DynamoKeySchemaElement> keySchema) throws C8DBException;

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
     * @param values The request params
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> putItem(Collection<T> values) throws C8DBException;

    /**
     * This method updates an existing item in the dynamo like table
     * @param values The request params
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> updateItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @param values The request params
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> getItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @param values The request params
     * @return The Dynamo entity
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> deleteItem(Collection<T> values) throws C8DBException;

    /**
     * This method returns an existing item from the dynamo like table
     * @param values The request params
     * @return The Dynamo entity with response parameters
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> getItems(Collection<T> values) throws C8DBException;

    /**
     * This method writes multiple items (or records) to the dynamo-like collection.
     * @param values List of items to be written in dynamo collection
     * @return The Dynamo entity with response parameters
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> batchWriteItem(Collection<T> values) throws C8DBException;;
}
