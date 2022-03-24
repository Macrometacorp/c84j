/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.C8Dynamo;
import com.c8db.entity.C8DynamoEntity;
import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;
import com.c8db.entity.C8DynamoDescribeEntity;
import com.c8db.entity.C8DynamoItemEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.C8DynamoDeleteEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.C8DynamoCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class C8DynamoImpl extends InternalC8Dynamo<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Dynamo {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8DynamoImpl.class);

    protected C8DynamoImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public C8DynamoEntity createTable(String tableName, List<DynamoAttributeDefinition> attributeDefinitionList,
                                      List<DynamoKeySchemaElement> keySchema) throws C8DBException {
        return executor.execute(createRequest(tableName, new C8DynamoCreateOptions(attributeDefinitionList,
                        tableName,keySchema)), getC8DynamoCreateTableResponseDeserializer());
    }

    @Override
    public C8DynamoDeleteEntity deleteTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDeleteRequest(tableName, options), getC8DynamoDeleteTableResponseDeserializer());
    }

    @Override
    public C8DynamoDescribeEntity describeTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDescribeRequest(tableName, options), getC8DynamoDescTableResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> putItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values), itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> updateItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values), itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> getItem(Collection<T> values) throws C8DBException {
        return executor.execute(getItemRequest(values), itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> deleteItem(Collection<T> values) throws C8DBException {
        return executor.execute(deleteItemRequest(values), itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> getItems(Collection<T> values) throws C8DBException {
        return executor.execute(getItemsRequest(values), itemsResponseDeserializer());
    }
}
