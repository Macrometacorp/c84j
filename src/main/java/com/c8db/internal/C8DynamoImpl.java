package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.C8Dynamo;
import com.c8db.entity.*;
import com.c8db.model.C8DynamoCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class C8DynamoImpl extends InternalC8Dynamo<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Dynamo {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8DynamoImpl.class);

    protected C8DynamoImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public C8DynamoEntity createTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createRequest(tableName, new C8DynamoCreateOptions(options)),
                getC8DynamoCreateTableResponseDeserializer());
    }

    @Override
    public C8DynamoDeleteEntity deleteTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDeleteRequest(tableName,options),
                getC8DynamoDeleteTableResponseDeserializer());
    }

    @Override
    public C8DynamoDescribeEntity describeTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDescribeRequest(tableName,options),
                getC8DynamoDescTableResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<C8DynamoItemEntity> putItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values),
                itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<C8DynamoItemEntity> updateItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values),
                itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<C8DynamoItemEntity> getItem(Collection<T> values) throws C8DBException {
        return executor.execute(getItemRequest(values),
                itemsResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<C8DynamoItemEntity> deleteItem(Collection<T> values) throws C8DBException {
        return executor.execute(deleteItemRequest(values),
                itemsResponseDeserializer());
    }
}
