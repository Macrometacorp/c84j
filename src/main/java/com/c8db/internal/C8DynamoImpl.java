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
    public C8DynamoEntity create(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createRequest(tableName, new C8DynamoCreateOptions(options)),
                getC8DynamoCreateTableResponseDeserializer());
    }

    @Override
    public C8DynamoDeleteEntity deleteTable(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDeleteRequest(tableName,options),
                getC8DynamoDeleteTableResponseDeserializer());
    }

    @Override
    public C8DynamoDescribeEntity describe(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createDescribeRequest(tableName,options),
                getC8DynamoDescTableResponseDeserializer());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> putItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values),
                insertItemsResponseDeserializer(values));
    }

    //@Override
    /*public JSONObject putItem(JSONObject options) throws C8DBException {
        return executor.execute(createPutItemRequest(options), JSONObject.class);
    }*/


    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> updateItem(Collection<T> values) throws C8DBException {
        return executor.execute(createPutItemRequest(values),
                insertItemsResponseDeserializer(values));
    }
}
