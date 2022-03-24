/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.internal;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.*;
import com.c8db.internal.util.C8SerializationFactory;
import com.c8db.model.C8DynamoCreateOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.util.C8Constants;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.*;

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
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY,C8Constants.C8_DYNAMO_CREATE_TABLE_VAL);
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoEntity> getC8DynamoCreateTableResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<C8DynamoEntity>() {
            @Override
            public C8DynamoEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                return util().deserialize(result,  new Type<C8DynamoEntity>(){}.getType());
            }
        };
    }

    protected Request createDeleteRequest(final String tableName, final C8DynamoCreateOptions options) {
        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new C8DynamoCreateOptions(), tableName));
        Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO).setBody(body);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY,C8Constants.C8_DYNAMO_DELETE_TABLE_VAL);
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoDeleteEntity> getC8DynamoDeleteTableResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<C8DynamoDeleteEntity>() {
            @Override
            public C8DynamoDeleteEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                return util().deserialize(result,  new Type<C8DynamoDeleteEntity>(){}.getType());
            }
        };
    }

    protected Request createDescribeRequest(final String tableName,final C8DynamoCreateOptions options) {
        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new C8DynamoCreateOptions(), tableName));
        Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO).setBody(body);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY,C8Constants.C8_DYNAMO_DESCRIBE_TABLE_VAL);
        return request;
    }

    protected C8Executor.ResponseDeserializer<C8DynamoDescribeEntity> getC8DynamoDescTableResponseDeserializer() {
        return new C8Executor.ResponseDeserializer<C8DynamoDescribeEntity>() {
            @Override
            public C8DynamoDescribeEntity deserialize(final Response response) throws VPackException {
                final VPackSlice result = response.getBody();
                return util().deserialize(result,  new Type<C8DynamoDescribeEntity>(){}.getType());
            }
        };
    }

    protected  <T> Request createPutItemRequest(final Collection<T> values) {
        final Request request = setRequestParams(values);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY, C8Constants.C8_DYNAMO_PUT_ITEM_VAL);
        System.out.println("Request : " + request.getBody());
        return request;
    }

    @SuppressWarnings("unchecked")
    protected  <T> C8Executor.ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<T>>> itemsResponseDeserializer() {
        return response -> {
            final MultiDocumentEntity<DocumentCreateEntity<T>> multiDocument = new MultiDocumentEntity<>();
            final Collection<DocumentCreateEntity<T>> docs = new ArrayList<>();
            final Collection<ErrorEntity> errors = new ArrayList<>();
            final Collection<Object> documentsAndErrors = new ArrayList<>();
            final VPackSlice body = response.getBody();
            if (body.isArray()) {
                for (final Iterator<VPackSlice> iterator = body.arrayIterator(); iterator.hasNext();) {
                    final VPackSlice next = iterator.next();
                    if (next.get(C8ResponseField.ERROR).isTrue()) {
                        final ErrorEntity error = (ErrorEntity) util().deserialize(next, ErrorEntity.class);
                        errors.add(error);
                        documentsAndErrors.add(error);
                    } else {
                        final DocumentCreateEntity<T> doc = util().deserialize(next, DocumentCreateEntity.class);
                        docs.add(doc);
                        documentsAndErrors.add(doc);
                    }
                }
            }else{
                final VPackSlice next = body;
                final DocumentCreateEntity<T> doc = util().deserialize(next, DocumentCreateEntity.class);
                docs.add(doc);
                documentsAndErrors.add(doc);
            }
            multiDocument.setDocuments(docs);
            multiDocument.setErrors(errors);
            multiDocument.setDocumentsAndErrors(documentsAndErrors);
            return multiDocument;
        };
    }

    protected  <T> Request getItemRequest(final Collection<T> values) {
        final Request request= setRequestParams(values);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY, C8Constants.C8_DYNAMO_GET_ITEM_VAL);
        return request;
    }

    protected  <T> Request deleteItemRequest(final Collection<T> values) {
        final Request request = setRequestParams(values);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY, C8Constants.C8_DYNAMO_DELETE_ITEM_VAL);
        return request;
    }

    protected  <T> Request getItemsRequest(final Collection<T> values) {
        final Request request= setRequestParams(values);
        request.putHeaderParam(C8Constants.C8_DYNAMO_HEADER_KEY, C8Constants.C8_DYNAMO_GET_ITEMS_VAL);
        return request;
    }

    private <T> Request setRequestParams(final Collection<T> values){
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DYNAMO);
        Iterator<T> items = values.iterator();
        T value = items.next();
        request.setBody(util(C8SerializationFactory.Serializer.CUSTOM).serialize(value,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));
        return request;
    }
}
