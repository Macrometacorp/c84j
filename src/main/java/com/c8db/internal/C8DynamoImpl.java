/*
 *  Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.C8Dynamo;
import com.c8db.entity.C8DynamoBatchWriteItemEntity;
import com.c8db.entity.C8DynamoCreateTableEntity;
import com.c8db.entity.C8DynamoDeleteItemEntity;
import com.c8db.entity.C8DynamoDescribeTableEntity;
import com.c8db.entity.C8DynamoGetItemEntity;
import com.c8db.entity.C8DynamoGetItemsEntity;
import com.c8db.entity.C8DynamoPutItemEntity;
import com.c8db.entity.C8DynamoDeleteTableEntity;
import com.c8db.model.C8DynamoCreateTableOptions;
import com.c8db.model.C8DynamoQueryOptions;
import com.c8db.model.C8DynamoScanOptions;
import com.c8db.model.C8DynamoUpdateTableOptions;

import java.util.Collection;
import java.util.Map;

public class C8DynamoImpl extends InternalC8Dynamo<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Dynamo {

    protected C8DynamoImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public C8DynamoCreateTableEntity createTable(C8DynamoCreateTableOptions options) throws C8DBException {
        return executor.execute(createTableRequest(tableName, options), C8DynamoCreateTableEntity.class);
    }

    @Override
    public C8DynamoCreateTableEntity updateTable(C8DynamoUpdateTableOptions options) throws C8DBException {
        return executor.execute(updateTableRequest(tableName, options), C8DynamoCreateTableEntity.class);
    }

    @Override
    public C8DynamoDeleteTableEntity deleteTable() throws C8DBException {
        return executor.execute(deleteTableRequest(tableName), C8DynamoDeleteTableEntity.class);
    }

    @Override
    public C8DynamoDescribeTableEntity describeTable() throws C8DBException {
        return executor.execute(createDescribeRequest(tableName), C8DynamoDescribeTableEntity.class);
    }

    @Override
    public C8DynamoPutItemEntity putItem(Map<String, Object> value) throws C8DBException {
        return executor.execute(createPutItemRequest(value), C8DynamoPutItemEntity.class);
    }

    @Override
    public C8DynamoBatchWriteItemEntity batchWriteItems(Collection<Map<String, Object>> values) throws C8DBException {
        return executor.execute(createBatchWriteItemRequest(values), getC8DynamoBatchWriteItemResponseDeserializer());
    }

    @Override
    public C8DynamoPutItemEntity updateItem(final Map<String, Object> value) throws C8DBException {
        return executor.execute(createPutItemRequest(value), C8DynamoPutItemEntity.class);
    }

    @Override
    public C8DynamoGetItemEntity getItem(Map<String, Object> key) throws C8DBException {
        return executor.execute(getItemRequest(key), getC8DynamoGetItemResponseDeserializer());
    }

    @Override
    public C8DynamoDeleteItemEntity deleteItem(Map<String, Object> key) throws C8DBException {
        return executor.execute(deleteItemRequest(key), C8DynamoDeleteItemEntity.class);
    }

    @Override
    public C8DynamoGetItemsEntity scan(C8DynamoScanOptions options) throws C8DBException {
        return executor.execute(scanRequest(options), getC8DynamoGetItemsResponseDeserializer());
    }

    @Override
    public C8DynamoGetItemsEntity query(C8DynamoQueryOptions options) throws C8DBException {
        return executor.execute(queryRequest(options), getC8DynamoGetItemsResponseDeserializer());
    }

}
