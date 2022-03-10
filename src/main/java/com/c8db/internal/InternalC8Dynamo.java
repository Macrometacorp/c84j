package com.c8db.internal;


import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.C8DynamoEntity;
import com.c8db.entity.LimitsEntity;
import com.c8db.entity.TenantMetricsEntity;
import com.c8db.model.C8DynamoCreateOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

public abstract class InternalC8Dynamo<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {
    protected static final String PATH_API_DYNAMO = "/_api/dynamo";
    private final D db;
    protected volatile String tableName;
    public D db() {
        return db;
    }

    protected InternalC8Dynamo(final D db, final String tableName) {
        super(db.executor, db.util, db.context);
        this.db = db;
        this.tableName = tableName;
    }

    protected Request createRequest(final String tableName, final C8DynamoCreateOptions options) {
        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new C8DynamoCreateOptions(), tableName));
        Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO, tableName).setBody(body);
        request.putHeaderParam("X-Amz-Target","DynamoDB_20120810.CreateTable");
        System.out.println("Request : " + request.getBody());
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoEntity> getC8DynamoTableResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<C8DynamoEntity>() {
            @Override
            public C8DynamoEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                System.out.println("response : " + response.getBody());
                return util().deserialize(result,  new Type<C8DynamoEntity>(){}.getType());
            }
        };
    }
}
