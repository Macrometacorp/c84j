/*
 * Copyright (c) 2023 - 2024 Macrometa Corp All rights reserved.
 */

package com.c8db.internal;

import static com.c8db.entity.DocumentField.Type.VALUE;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8DBException;
import com.c8db.entity.*;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.*;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.BinaryRequestBody;
import com.c8db.velocystream.JsonRequestBody;
import com.c8db.velocystream.MultipartResponseBody;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestBody;
import com.c8db.velocystream.RequestType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public abstract class InternalC8KeyValue<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    protected static final String PATH_API_KV = "/_api/kv";
    protected static final String PATH_API_KV_KEYS = "keys";
    protected static final String PATH_API_KV_PAIR = "value";
    protected static final String PATH_API_KV_PAIRS = "values";
    protected static final String PATH_API_KV_COUNT = "count";
    protected static final String PATH_API_KV_TRUNCATE = "truncate";
    protected static final String PATH_API_KV_GROUPS = "groups";
    protected static final String PATH_API_KV_GROUP_ID = "groupID";

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String ORDER = "order";
    private static final String EXPIRATION = "expiration";
    private static final String GROUP_ID = "groupID";
    private static final String GROUP = "group";
    private static final String STRONG_CONSISTENCY = "strongConsistency";
    public static final String TRANSACTION_ID = "x-gdn-trxid";

    private static final String NEW = "new";
    private static final String OLD = "old";

    private final D db;
    protected volatile String name;

    protected InternalC8KeyValue(final D db, final String name) {
        super(db.executor, db.util, db.context, db.tenant());
        this.db = db;
        this.name = name;
    }

    public D db() {
        return db;
    }

    public String name() {
        return name;
    }

    protected Request insertKVPairsRequest(final Collection<BaseKeyValue> values, C8KVInsertValuesOptions options) {
        final C8KVInsertValuesOptions params = (options != null ? options : new C8KVInsertValuesOptions());
        RequestBody body = new JsonRequestBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));

        return request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_PAIR)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId())
                .setBody(body);
    }

    protected  <T> Request insertBlobKVPairsRequest(final Collection<BlobKeyValue> values, C8KVInsertValuesOptions options) {
        final C8KVInsertValuesOptions params = (options != null ? options : new C8KVInsertValuesOptions());
        List<BinaryRequestBody.Item> binaryItems = values.stream().map(item -> {
            VPackSlice meta = util(Serializer.CUSTOM).serialize(item,
                    new C8Serializer.Options().serializeNullValues(false).stringAsJson(true));
            return new BinaryRequestBody.Item(meta, item.getValue());
        }).collect(Collectors.toList());

        return request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_PAIR)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId())
                .setBody(new BinaryRequestBody(binaryItems));
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

    protected  ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<BlobKeyValue>>> insertBlobKVPairsResponseDeserializer() {
        return response -> {
            final MultiDocumentEntity<DocumentCreateEntity<BlobKeyValue>> multiDocument = new MultiDocumentEntity<>();
            final Collection<DocumentCreateEntity<BlobKeyValue>> docs = new ArrayList<>();
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
                        final DocumentCreateEntity<BlobKeyValue> doc = util().deserialize(next, DocumentCreateEntity.class);
                        final VPackSlice newDoc = next.get(NEW);
                        if (newDoc.isObject()) {
                            doc.setNew(util(Serializer.CUSTOM).deserialize(newDoc, BlobKeyValue.class));
                        }
                        final VPackSlice oldDoc = next.get(OLD);
                        if (oldDoc.isObject()) {
                            doc.setOld(util(Serializer.CUSTOM).deserialize(oldDoc, BlobKeyValue.class));
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
        final C8KVReadValueOptions params = (options != null ? options : new C8KVReadValueOptions());
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_PAIR, key)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
    }

    protected Request getBlobKVPairRequest(final String key, C8KVReadValueOptions options) {
        final C8KVReadValueOptions params = (options != null ? options : new C8KVReadValueOptions());
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_PAIR, key)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId())
                .putHeaderParam(HttpHeaders.ACCEPT, ContentType.MULTIPART_FORM_DATA.getMimeType());
    }

    protected Request countKVPairsRequest(C8KVCountPairsOptions options) {
        final C8KVCountPairsOptions params = (options != null ? options : new C8KVCountPairsOptions());
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_COUNT)
                .putQueryParam(GROUP_ID, params.getGroup())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
    }

    protected ResponseDeserializer<Long> countKVPairsResponseDeserializer() {
        return response ->  response.getBody().get("count").getAsLong();
    }

    protected Request getKVPairsRequest(final C8KVReadValuesOptions options) {
        final C8KVReadValuesOptions params = (options != null ? options : new C8KVReadValuesOptions());
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name, PATH_API_KV_PAIRS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .putQueryParam(GROUP_ID, params.getGroup())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        if (ObjectUtils.isNotEmpty(params.getKeys())) {
            request.setBody(util().serialize(params.getKeys()));
        }
        return request;
    }

    protected Request getBlobKVPairsRequest(final C8KVReadValuesOptions options) {
        final C8KVReadValuesOptions params = (options != null ? options : new C8KVReadValuesOptions());
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_KV, name, PATH_API_KV_PAIRS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .putQueryParam(GROUP_ID, params.getGroup())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId())
                .putHeaderParam(HttpHeaders.ACCEPT, ContentType.MULTIPART_FORM_DATA.getMimeType());
        if (ObjectUtils.isNotEmpty(params.getKeys())) {
            request.setBody(util().serialize(params.getKeys()));
        }
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

    protected ResponseDeserializer<MultiDocumentEntity<BlobKeyValue>> getBlobKVPairsResponseDeserializer() {
        return response -> {
            final MultiDocumentEntity<BlobKeyValue> multiDocument = new MultiDocumentEntity<>();
            final Collection<BlobKeyValue> docs = new ArrayList<>();
            final Collection<ErrorEntity> errors = new ArrayList<>();
            final Collection<Object> documentsAndErrors = new ArrayList<>();
            Collection<MultipartResponseBody.Item> items = response.getMultipartBody().getItems();
            BlobKeyValue last = null;
            for (MultipartResponseBody.Item item : items) {
                if (item.getContentType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
                    final VPackSlice next = (VPackSlice) item.getValue();
                    if (next.get(C8ResponseField.ERROR).isTrue()) {
                        final ErrorEntity error = util().deserialize(next, ErrorEntity.class);
                        errors.add(error);
                        documentsAndErrors.add(error);
                    } else {
                        last = util(Serializer.CUSTOM).deserialize(next, BlobKeyValue.class);
                        VPackSlice valueVpack = next.get(VALUE.getSerializeName());
                        if (valueVpack != null) {
                            last.setValue(valueVpack.getAsString().getBytes(StandardCharsets.UTF_8));
                        }
                        docs.add(last);
                        documentsAndErrors.add(last);
                    }
                } else if (item.getContentType().equals(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
                    if (last != null) {
                        last.setValue((byte[]) item.getValue());
                    } else {
                        throw new C8DBException("Content type 'application/json' must precede this type " +
                                "'application/octet-stream'");
                    }
                }

            }
            multiDocument.setDocuments(docs);
            multiDocument.setErrors(errors);
            multiDocument.setDocumentsAndErrors(documentsAndErrors);
            return multiDocument;
        };
    }

    protected ResponseDeserializer<BlobKeyValue> getBlobKVPairResponseDeserializer() {
        return response -> {
            BlobKeyValue last = null;
            if (response.getMultipartBody() != null) {
                for (MultipartResponseBody.Item item : response.getMultipartBody().getItems()) {
                    if (item.getContentType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
                        final VPackSlice next = (VPackSlice) item.getValue();
                        last = util(Serializer.CUSTOM).deserialize(next, BlobKeyValue.class);
                        VPackSlice valueVpack = next.get(VALUE.getSerializeName());
                        if (valueVpack != null) {
                            last.setValue(valueVpack.getAsString().getBytes(StandardCharsets.UTF_8));
                        }
                    } else if (item.getContentType().equals(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) {
                        if (last != null) {
                            last.setValue((byte[]) item.getValue());
                        } else {
                            throw new C8DBException("Content type 'application/json' must precede this type " +
                                    "'application/octet-stream'");
                        }
                    }
                }
            }
            return last;
        };
    }

    protected Request getKVKeysRequest(final C8KVReadKeysOptions options) {
        final C8KVReadKeysOptions params = (options != null ? options : new C8KVReadKeysOptions());
       return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_KEYS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .putQueryParam(ORDER, params.getOrder())
                .putQueryParam(GROUP_ID, params.getGroup())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
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
        final C8KVDeleteValueOptions params = (options != null ? options : new C8KVDeleteValueOptions());
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, PATH_API_KV_PAIR, key)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
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
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name, PATH_API_KV_PAIRS)
                .putQueryParam(STRONG_CONSISTENCY, options != null && options.hasStrongConsistency())
                .setBody(util().serialize(keys));
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
        final C8KVTruncateOptions params = (options != null ? options : new C8KVTruncateOptions());
        return request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_TRUNCATE)
                .putQueryParam(GROUP_ID, params.getGroup())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
    }

    protected Request dropRequest(C8KVDropOptions options) {
        final C8KVDropOptions params = (options != null ? options : new C8KVDropOptions());
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_KV, name)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
    }

    protected Request getAllGroupsRequest(C8KVReadGroupsOptions options) {
        final C8KVReadGroupsOptions params = (options != null ? options : new C8KVReadGroupsOptions());
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_KV, name, PATH_API_KV_GROUPS)
                .putQueryParam(OFFSET, params.getOffset())
                .putQueryParam(LIMIT, params.getLimit())
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency());
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

    protected Request updateGroupRequest(String oldGroupID, String newGroupID, C8KVUpdateGroupOptions options) {
        final C8KVUpdateGroupOptions params = (options != null ? options : new C8KVUpdateGroupOptions());
        Map<String, Object> payload = new HashMap<>();
        payload.put("oldGroupID", oldGroupID);
        payload.put("newGroupID", newGroupID);
        VPackSlice body = util().serialize(payload);
        return request(db.tenant(), db.name(), RequestType.PUT, PATH_API_KV, name, PATH_API_KV_GROUP_ID)
                .putQueryParam(STRONG_CONSISTENCY, params.hasStrongConsistency())
                .setBody(body);
    }

}
