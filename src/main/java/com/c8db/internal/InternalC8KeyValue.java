/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.entity.*;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.*;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class InternalC8KeyValue<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_KV = "/_api/kv";
    protected static final String PATH_API_KV_KEYS = "keys";
    protected static final String PATH_API_KV_PAIR = "value";
    protected static final String PATH_API_KV_PAIRS = "values";
    protected static final String PATH_API_KV_COUNT = "count";
    protected static final String PATH_API_KV_TRUNCATE = "truncate";
    protected static final String PATH_API_KV_GROUPS = "groups";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String ORDER = "order";
    private static final String EXPIRATION = "expiration";
    private static final String GROUP_ID = "groupID";
    private static final String GROUP = "group";
    private static final String STRONG_CONSISTENCY = "strongConsistency";


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

    protected  <T> Request insertKVPairsRequest(final Collection<T> values, C8KVInsertValuesOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_PAIR);
        request.putQueryParam(STRONG_CONSISTENCY, options != null && options.hasStrongConsistency());
        request.setBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));
        return request;
    }

    @SuppressWarnings("unchecked")
    protected  ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>>> insertKVPairsResponseDeserializer() {
        return response -> {
            final MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> multiDocument = new MultiDocumentEntity<>();
            final Collection<DocumentCreateEntity<BaseKeyValue>> docs = new ArrayList<>();
            final Collection<ErrorEntity> errors = new ArrayList<>();
            final Collection<Object> documentsAndErrors = new ArrayList<>();
            final VPackSlice body = response.getBody();
            if (body.isArray()) {
                for (final Iterator<VPackSlice> iterator = body.arrayIterator(); iterator.hasNext();) {
                    final VPackSlice next = iterator.next();
                    if (next.get(C8ResponseField.ERROR).isTrue()) {
                        final ErrorEntity error = util().deserialize(next, ErrorEntity.class);
                        errors.add(error);
                        documentsAndErrors.add(error);
                    } else {
                        final DocumentCreateEntity<BaseKeyValue> doc = util().deserialize(next, DocumentCreateEntity.class);
                        final VPackSlice newDoc = next.get(NEW);
                        if (newDoc.isObject()) {
                            doc.setNew(util(Serializer.CUSTOM).deserialize(newDoc, BaseKeyValue.class));
                        }
                        final VPackSlice oldDoc = next.get(OLD);
                        if (oldDoc.isObject()) {
                            doc.setOld(util(Serializer.CUSTOM).deserialize(oldDoc, BaseKeyValue.class));
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

    protected Request getKVPairRequest(final String key, C8KVReadValueOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_PAIR,
                key);
        request.putQueryParam(STRONG_CONSISTENCY, options != null && options.hasStrongConsistency());
        return request;
    }

    protected Request getAllCollections() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV);
    }

    protected ResponseDeserializer<Collection<C8KVCollectionEntity>> getAllCollectionsResponseDeserializer() {
        return response -> {
            Collection<C8KVCollectionEntity> coll = new ArrayList<>();
            final VPackSlice kvs = response.getBody().get("result");
            for (final Iterator<VPackSlice> iterator = kvs.arrayIterator(); iterator.hasNext();) {
                final VPackSlice next = iterator.next();
                final C8KVCollectionEntity entity = util(Serializer.CUSTOM).deserialize(next, C8KVCollectionEntity.class);
                coll.add(entity);
            }
            return coll;
        };
    }

    protected Request countKVPairsRequest(C8KVCountPairsOptions options) {
        final Request request =  request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_COUNT);
        final C8KVCountPairsOptions params = (options != null ? options : new C8KVCountPairsOptions());
        if (StringUtils.isNotEmpty(params.getGroup())){
            request.putQueryParam(GROUP_ID, params.getGroup());
        }
        request.putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
        return request;
    }

    protected ResponseDeserializer<Long> countKVPairsResponseDeserializer() {
        return response ->  response.getBody().get("count").getAsLong();
    }

    protected Request getKVPairsRequest(final C8KVReadValuesOptions options) {
        final C8KVReadValuesOptions params = (options != null ? options : new C8KVReadValuesOptions());
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name, PATH_API_KV_PAIRS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit());
        if (params.getKeys() != null && !params.getKeys().isEmpty()) {
            request.setBody(util().serialize(params.getKeys()));
        }
        if (StringUtils.isNotEmpty(params.getGroup())) {
            request.putQueryParam(GROUP_ID, params.getGroup());
        }
        request.putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
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

    protected Request getKVKeysRequest(final C8KVReadKeysOptions options) {
        final C8KVReadKeysOptions params = (options != null ? options : new C8KVReadKeysOptions());
        Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_KEYS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .putQueryParam(ORDER, params.getOrder());
        if (StringUtils.isNotEmpty(params.getGroup())) {
            request.putQueryParam(GROUP_ID, params.getGroup());
        }
        request.putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
        return request;
    }

    protected ResponseDeserializer<Collection<String>> getKVKeysResponseDeserializer() {
        return response -> {
            Collection<String> coll = new ArrayList<>();
            final VPackSlice kvs = response.getBody().get("result");
            for (final Iterator<VPackSlice> iterator = kvs.arrayIterator(); iterator.hasNext();) {
                final VPackSlice next = iterator.next();
                coll.add(next.getAsString());
            }
            return coll;
        };
    }

    protected Request deleteKVPairRequest(final String key, C8KVDeleteValueOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, PATH_API_KV_PAIR,
                key);
        request.putQueryParam(STRONG_CONSISTENCY, options != null && options.hasStrongConsistency());
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

    protected <T> Request deleteKVPairsRequest(final Collection<T> keys, C8KVDeleteValuesOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, PATH_API_KV_PAIRS);
        request.putQueryParam(STRONG_CONSISTENCY, options != null && options.hasStrongConsistency());
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

    protected Request createRequest(final String name, final C8KVCreateOptions options) {
        VPackSlice body = util()
                .serialize(options != null ? OptionsBuilder.build(options) : new C8KVCreateBodyOptions());

        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name);
        request.putQueryParam(EXPIRATION, options != null && options.hasExpiration());
        request.putQueryParam(GROUP, options != null && options.hasGroup());
        request.setBody(body);
        return request;
    }

    protected Request truncateRequest(C8KVTruncateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_TRUNCATE);
        final C8KVTruncateOptions params = (options != null ? options : new C8KVTruncateOptions());
        if (StringUtils.isNotEmpty(params.getGroup())) {
            request.putQueryParam(GROUP_ID, params.getGroup());
        }
        request.putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
        return request;
    }

    protected Request dropRequest() {
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name);
    }

    protected Request getAllGroups() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_GROUPS);
    }

    protected ResponseDeserializer<Collection<String>> getAllGroupsResponseDeserializer() {
        return response -> {
            Collection<String> coll = new ArrayList<>();
            final VPackSlice kvs = response.getBody().get("groups");
            for (final Iterator<VPackSlice> iterator = kvs.arrayIterator(); iterator.hasNext();) {
                final VPackSlice next = iterator.next();
                coll.add(next.getAsString());
            }
            return coll;
        };
    }

}
