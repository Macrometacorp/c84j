package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.C8Dynamo;
import com.c8db.entity.C8DynamoEntity;
import com.c8db.model.C8DynamoCreateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C8DynamoImpl extends InternalC8Dynamo<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Dynamo {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8DynamoImpl.class);

    protected C8DynamoImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public C8DynamoEntity create(C8DynamoCreateOptions options) throws C8DBException {
        return executor.execute(createRequest(tableName, new C8DynamoCreateOptions()), C8DynamoEntity.class);
    }
}
