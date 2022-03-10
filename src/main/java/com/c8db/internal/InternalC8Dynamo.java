package com.c8db.internal;


import com.arangodb.velocypack.VPackSlice;
import com.c8db.model.C8DynamoCreateOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

public abstract class InternalC8Dynamo<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {
    protected static final String PATH_API_DYNAMO = "/dynamo";
    private final D db;
    protected volatile String tableName;
    public D db() {
        return db;
    }

    protected InternalC8Dynamo(final D db, final String name) {
        super(db.executor, db.util, db.context);
        this.db = db;
    }

    protected Request createRequest(final String tableName, final C8DynamoCreateOptions options) {
        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new C8DynamoCreateOptions(), tableName));
        return request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO, tableName).setBody(body);
    }
}
