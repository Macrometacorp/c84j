/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.entity.ErrorEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.entity.BaseKeyValue;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.model.*;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import java.util.*;

public abstract class InternalC8KeyValue<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_KV = "/kv";
    protected static final String PATH_API_KV_PAIR = "/value";
    protected static final String PATH_API_KV_PAIRS = "/values";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String EXPIRATION = "expiration";

    private static final String NEW = "new";
    private static final String OLD = "old";

    private final D db;
    protected volatile String name;

    protected InternalC8KeyValue(final D db, final String name) {
        super(db.executor, db.util, db.context);
        this.db = db;
        this.name = name;
    }

    public D db() {
        return db;
    }

    public String name() {
        return name;
    }

    protected  <T> Request insertKVPairsRequest(final Collection<T> values, final DocumentCreateOptions params) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, "value");
        request.setBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));
        return request;
    }

    @SuppressWarnings("unchecked")
    protected  <T> ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<T>>> insertKVPairsResponseDeserializer(
            final Collection<T> values, final DocumentCreateOptions params) {
        return response -> {
            Class<T> type = null;
            if (Boolean.TRUE == params.getReturnNew()) {
                if (!values.isEmpty()) {
                    type = (Class<T>) values.iterator().next().getClass();
                }
            }
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
                        final VPackSlice newDoc = next.get(NEW);
                        if (newDoc.isObject()) {
                            doc.setNew((T) util(Serializer.CUSTOM).deserialize(newDoc, type));
                        }
                        final VPackSlice oldDoc = next.get(OLD);
                        if (oldDoc.isObject()) {
                            doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, type));
                        }
                        docs.add(doc);
                        documentsAndErrors.add(doc);
                    }
                }
            }
            multiDocument.setDocuments(docs);
            multiDocument.setErrors(errors);
            multiDocument.setDocumentsAndErrors(documentsAndErrors);
            return multiDocument;
        };
    }

    protected Request getKVPairRequest(final String key) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, "value",
                key);
        return request;
    }

    protected Request getKVPairsRequest(final Collection<String> keys, final C8KVPairReadOptions options) {
        final C8KVPairReadOptions params = (options != null ? options : new C8KVPairReadOptions());
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name, "values")
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .setBody(util().serialize(keys));
        return request;
    }

    protected <T> ResponseDeserializer<MultiDocumentEntity<T>> getKVPairsResponseDeserializer() {
        return response -> {
            final MultiDocumentEntity<T> multiDocument = new MultiDocumentEntity<>();
            final Collection<T> docs = new ArrayList<>();
            final Collection<ErrorEntity> errors = new ArrayList<>();
            final Collection<Object> documentsAndErrors = new ArrayList<>();
            final VPackSlice kvs = response.getBody().get("result");
            for (final Iterator<VPackSlice> iterator = kvs.arrayIterator(); iterator.hasNext();) {
                final VPackSlice next = iterator.next();
                if (next.get(C8ResponseField.ERROR).isTrue()) {
                    final ErrorEntity error = util().deserialize(next, ErrorEntity.class);
                    errors.add(error);
                    documentsAndErrors.add(error);
                } else {
                    final T doc = util(Serializer.CUSTOM).deserialize(next, BaseKeyValue.class);
                    docs.add(doc);
                    documentsAndErrors.add(doc);
                }
            }
            multiDocument.setDocuments(docs);
            multiDocument.setErrors(errors);
            multiDocument.setDocumentsAndErrors(documentsAndErrors);
            return multiDocument;
        };
    }

    protected Request deleteKVPairRequest(final String key, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, "value",
                key);
        return request;
    }

    protected <T> ResponseDeserializer<DocumentDeleteEntity<T>> deleteKVPairResponseDeserializer(
            final Class<T> type) {
        return response -> {
            final VPackSlice body = response.getBody();
            final DocumentDeleteEntity<T> doc = util().deserialize(body, DocumentDeleteEntity.class);
            final VPackSlice oldDoc = body.get(OLD);
            if (oldDoc.isObject()) {
                doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, type));
            }
            return doc;
        };
    }

    protected <T> Request deleteKVPairsRequest(final Collection<T> keys, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, "values");
        request.setBody(util().serialize(keys));
        return request;
    }

    protected <T> ResponseDeserializer<MultiDocumentEntity<DocumentDeleteEntity<T>>> deleteKVPairsResponseDeserializer(
            final Class<T> type) {
        return response -> {
            final MultiDocumentEntity<DocumentDeleteEntity<T>> multiDocument = new MultiDocumentEntity<>();
            final Collection<DocumentDeleteEntity<T>> docs = new ArrayList<DocumentDeleteEntity<T>>();
            final Collection<ErrorEntity> errors = new ArrayList<ErrorEntity>();
            final Collection<Object> documentsAndErrors = new ArrayList<Object>();
            final VPackSlice body = response.getBody();
            if (body.isArray()) {
                for (final Iterator<VPackSlice> iterator = body.arrayIterator(); iterator.hasNext();) {
                    final VPackSlice next = iterator.next();
                    if (next.get(C8ResponseField.ERROR).isTrue()) {
                        final ErrorEntity error = (ErrorEntity) util().deserialize(next, ErrorEntity.class);
                        errors.add(error);
                        documentsAndErrors.add(error);
                    } else {
                        final DocumentDeleteEntity<T> doc = util().deserialize(next, DocumentDeleteEntity.class);
                        final VPackSlice oldDoc = next.get(OLD);
                        if (oldDoc.isObject()) {
                            doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, type));
                        }
                        docs.add(doc);
                        documentsAndErrors.add(doc);
                    }
                }
            }
            multiDocument.setDocuments(docs);
            multiDocument.setErrors(errors);
            multiDocument.setDocumentsAndErrors(documentsAndErrors);
            return multiDocument;
        };
    }

    protected Request createRequest(final String name, final Boolean expiration, final CollectionCreateOptions options) {

        VPackSlice body = util()
                .serialize(OptionsBuilder.build(options != null ? options : new CollectionCreateOptions(), name));

        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name);
        request.putQueryParam(EXPIRATION, expiration);
        request.setBody(body);
        return request;
    }

    protected Request truncateRequest() {
        return request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, "truncate");
    }

    protected Request dropRequest() {
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name);
    }

}
