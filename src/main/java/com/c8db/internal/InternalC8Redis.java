/*
 * Copyright (c) 2022 - 2024 Macrometa Corp All rights reserved
 */
package com.c8db.internal;

import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.protocol.json.SdkJsonProtocolFactory;
import com.arangodb.velocypack.VPackSlice;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.C8RedisCreateOptions;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;

import java.util.HashMap;
import java.util.Map;

public abstract class InternalC8Redis<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {
    protected static final String PATH_API_REDIS = "/_api/collection";
    private final D db;
    protected volatile String tableName;
    protected SdkJsonProtocolFactory protocolFactory;

    public D db() {
        return db;
    }

    protected InternalC8Redis(final D db, final String tableName) {
        super(db.executor, db.util, db.context, db.tenant());
        this.db = db;
        this.tableName = tableName;
        this.protocolFactory = new SdkJsonProtocolFactory((new JsonClientMetadata()));
    }

    protected Request createTableRequest(final String tableName, final C8RedisCreateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_REDIS);

        Map<String, Object> internalOptions = new HashMap<>();
        internalOptions.put("name", tableName);
        internalOptions.put("collectionModel", 3);
        internalOptions.put("stream", options.getStream());
        VPackSlice slice = util(C8SerializationFactory.Serializer.CUSTOM).serialize(internalOptions,
            new C8Serializer.Options().serializeNullValues(false).stringAsJson(true));
        request.setBody(slice);
        return request;
    }






}
