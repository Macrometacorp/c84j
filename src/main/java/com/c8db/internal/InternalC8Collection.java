/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications copyright (c) 2021 Macrometa Corp All rights reserved.
 *
 */

package com.c8db.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DBException;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.entity.DocumentField;
import com.c8db.entity.DocumentUpdateEntity;
import com.c8db.entity.ErrorEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.entity.Permissions;
import com.c8db.internal.C8Executor.ResponseDeserializer;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.internal.util.C8SerializationFactory.Serializer;
import com.c8db.model.CollectionCountOptions;
import com.c8db.model.CollectionPropertiesOptions;
import com.c8db.model.CollectionRenameOptions;
import com.c8db.model.CollectionTruncateOptions;
import com.c8db.model.DocumentCreateOptions;
import com.c8db.model.DocumentDeleteOptions;
import com.c8db.model.DocumentExistsOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.DocumentReplaceOptions;
import com.c8db.model.DocumentUpdateOptions;
import com.c8db.model.FulltextIndexOptions;
import com.c8db.model.GeoIndexOptions;
import com.c8db.model.HashIndexOptions;
import com.c8db.model.OptionsBuilder;
import com.c8db.model.PersistentIndexOptions;
import com.c8db.model.SkiplistIndexOptions;
import com.c8db.model.TTLIndexOptions;
import com.c8db.model.UserAccessOptions;
import com.c8db.util.C8Serializer;
import com.c8db.velocystream.Request;
import com.c8db.velocystream.RequestType;
import com.c8db.velocystream.Response;

import static com.c8db.internal.InternalC8Database.PATH_API_USER;

/**
 */
public abstract class InternalC8Collection<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
        extends C8Executeable<E> {

    private static final String COLLECTION_QUERY_PARAM = "collection";

    protected static final String PATH_API_COLLECTION = "/_api/collection";
    protected static final String PATH_API_DOCUMENT = "/_api/document";
    protected static final String PATH_API_INDEX = "/_api/index";

    private static final String MERGE_OBJECTS = "mergeObjects";
    private static final String IGNORE_REVS = "ignoreRevs";
    private static final String RETURN_NEW = "returnNew";
    private static final String NEW = "new";
    private static final String RETURN_OLD = "returnOld";
    private static final String OVERWRITE = "overwrite";
    private static final String OLD = "old";
    private static final String SILENT = "silent";

    public static final String TRANSACTION_ID = "x-gdn-trxid";

    private final D db;
    protected volatile String name;

    protected InternalC8Collection(final D db, final String name) {
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

    protected <T> Request insertDocumentRequest(final T value, final DocumentCreateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DOCUMENT, name);
        final DocumentCreateOptions params = (options != null ? options : new DocumentCreateOptions());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.putQueryParam(OVERWRITE, params.getOverwrite());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected <T> ResponseDeserializer<DocumentCreateEntity<T>> insertDocumentResponseDeserializer(final T value,
            final DocumentCreateOptions options) {
        return new ResponseDeserializer<DocumentCreateEntity<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public DocumentCreateEntity<T> deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                final DocumentCreateEntity<T> doc = util().deserialize(body, DocumentCreateEntity.class);
                final VPackSlice newDoc = body.get(NEW);
                if (newDoc.isObject()) {
                    doc.setNew((T) util(Serializer.CUSTOM).deserialize(newDoc, value.getClass()));
                }
                final VPackSlice oldDoc = body.get(OLD);
                if (oldDoc.isObject()) {
                    doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, value.getClass()));
                }
                if (options == null || Boolean.TRUE != options.getSilent()) {
                    final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                    values.put(DocumentField.Type.ID, doc.getId());
                    values.put(DocumentField.Type.KEY, doc.getKey());
                    values.put(DocumentField.Type.REV, doc.getRev());
                    executor.documentCache().setValues(value, values);
                }
                return doc;
            }
        };
    }

    protected <T> Request insertDocumentsRequest(final Collection<T> values, final DocumentCreateOptions params) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_DOCUMENT, name);
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.putQueryParam(OVERWRITE, params.getOverwrite());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.setBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));
        return request;
    }

    @SuppressWarnings("unchecked")
    protected <T> ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<T>>> insertDocumentsResponseDeserializer(
            final Collection<T> values, final DocumentCreateOptions params) {
        return new ResponseDeserializer<MultiDocumentEntity<DocumentCreateEntity<T>>>() {
            @Override
            public MultiDocumentEntity<DocumentCreateEntity<T>> deserialize(final Response response)
                    throws VPackException {
                Class<T> type = null;
                if (Boolean.TRUE == params.getReturnNew()) {
                    if (!values.isEmpty()) {
                        type = (Class<T>) values.iterator().next().getClass();
                    }
                }
                final MultiDocumentEntity<DocumentCreateEntity<T>> multiDocument = new MultiDocumentEntity<DocumentCreateEntity<T>>();
                final Collection<DocumentCreateEntity<T>> docs = new ArrayList<DocumentCreateEntity<T>>();
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
            }
        };
    }

    protected Request getDocumentRequest(final String key, final DocumentReadOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_DOCUMENT,
                DocumentUtil.createDocumentHandle(name, key));
        final DocumentReadOptions params = (options != null ? options : new DocumentReadOptions());
        request.putHeaderParam(C8RequestParam.IF_NONE_MATCH, params.getIfNoneMatch());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        return request;
    }

    protected Request getDocumentsRequest(final Collection<String> keys, final DocumentReadOptions options) {
        final DocumentReadOptions params = (options != null ? options : new DocumentReadOptions());
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_DOCUMENT, name)
                .putQueryParam("onlyget", true)
                .putHeaderParam(C8RequestParam.IF_NONE_MATCH, params.getIfNoneMatch())
                .putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch()).setBody(util().serialize(keys))
                .putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        return request;
    }

    protected <T> ResponseDeserializer<MultiDocumentEntity<T>> getDocumentsResponseDeserializer(final Class<T> type,
            final DocumentReadOptions options) {
        return new ResponseDeserializer<MultiDocumentEntity<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public MultiDocumentEntity<T> deserialize(final Response response) throws VPackException {
                final MultiDocumentEntity<T> multiDocument = new MultiDocumentEntity<T>();
                final Collection<T> docs = new ArrayList<T>();
                final Collection<ErrorEntity> errors = new ArrayList<ErrorEntity>();
                final Collection<Object> documentsAndErrors = new ArrayList<Object>();
                final VPackSlice body = response.getBody();
                for (final Iterator<VPackSlice> iterator = body.arrayIterator(); iterator.hasNext();) {
                    final VPackSlice next = iterator.next();
                    if (next.get(C8ResponseField.ERROR).isTrue()) {
                        final ErrorEntity error = (ErrorEntity) util().deserialize(next, ErrorEntity.class);
                        errors.add(error);
                        documentsAndErrors.add(error);
                    } else {
                        final T doc = (T) util(Serializer.CUSTOM).deserialize(next, type);
                        docs.add(doc);
                        documentsAndErrors.add(doc);
                    }
                }
                multiDocument.setDocuments(docs);
                multiDocument.setErrors(errors);
                multiDocument.setDocumentsAndErrors(documentsAndErrors);
                return multiDocument;
            }
        };
    }

    protected <T> Request replaceDocumentRequest(final String key, final T value,
            final DocumentReplaceOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_DOCUMENT,
                DocumentUtil.createDocumentHandle(name, key));
        final DocumentReplaceOptions params = (options != null ? options : new DocumentReplaceOptions());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(IGNORE_REVS, params.getIgnoreRevs());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected <T> ResponseDeserializer<DocumentUpdateEntity<T>> replaceDocumentResponseDeserializer(final T value,
            final DocumentReplaceOptions options) {
        return new ResponseDeserializer<DocumentUpdateEntity<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public DocumentUpdateEntity<T> deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                final DocumentUpdateEntity<T> doc = util().deserialize(body, DocumentUpdateEntity.class);
                final VPackSlice newDoc = body.get(NEW);
                if (newDoc.isObject()) {
                    doc.setNew((T) util(Serializer.CUSTOM).deserialize(newDoc, value.getClass()));
                }
                final VPackSlice oldDoc = body.get(OLD);
                if (oldDoc.isObject()) {
                    doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, value.getClass()));
                }
                if (options == null || Boolean.TRUE != options.getSilent()) {
                    final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                    values.put(DocumentField.Type.REV, doc.getRev());
                    executor.documentCache().setValues(value, values);
                }
                return doc;
            }
        };
    }

    protected <T> Request replaceDocumentsRequest(final Collection<T> values, final DocumentReplaceOptions params) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_DOCUMENT, name);
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(IGNORE_REVS, params.getIgnoreRevs());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options().serializeNullValues(false).stringAsJson(true)));
        return request;
    }

    @SuppressWarnings("unchecked")
    protected <T> ResponseDeserializer<MultiDocumentEntity<DocumentUpdateEntity<T>>> replaceDocumentsResponseDeserializer(
            final Collection<T> values, final DocumentReplaceOptions params) {
        return new ResponseDeserializer<MultiDocumentEntity<DocumentUpdateEntity<T>>>() {
            @Override
            public MultiDocumentEntity<DocumentUpdateEntity<T>> deserialize(final Response response)
                    throws VPackException {
                Class<T> type = null;
                if (Boolean.TRUE == params.getReturnNew() || Boolean.TRUE == params.getReturnOld()) {
                    if (!values.isEmpty()) {
                        type = (Class<T>) values.iterator().next().getClass();
                    }
                }
                final MultiDocumentEntity<DocumentUpdateEntity<T>> multiDocument = new MultiDocumentEntity<DocumentUpdateEntity<T>>();
                final Collection<DocumentUpdateEntity<T>> docs = new ArrayList<DocumentUpdateEntity<T>>();
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
                            final DocumentUpdateEntity<T> doc = util().deserialize(next, DocumentUpdateEntity.class);
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
            }
        };
    }

    protected <T> Request updateDocumentRequest(final String key, final T value, final DocumentUpdateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PATCH, PATH_API_DOCUMENT,
                DocumentUtil.createDocumentHandle(name, key));
        final DocumentUpdateOptions params = (options != null ? options : new DocumentUpdateOptions());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.KEEP_NULL, params.getKeepNull());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(MERGE_OBJECTS, params.getMergeObjects());
        request.putQueryParam(IGNORE_REVS, params.getIgnoreRevs());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util(Serializer.CUSTOM).serialize(value, new C8Serializer.Options()
                .serializeNullValues(params.getSerializeNull() == null || params.getSerializeNull())));
        return request;
    }

    protected <T> ResponseDeserializer<DocumentUpdateEntity<T>> updateDocumentResponseDeserializer(final T value,
            final DocumentUpdateOptions options) {
        return new ResponseDeserializer<DocumentUpdateEntity<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public DocumentUpdateEntity<T> deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                final DocumentUpdateEntity<T> doc = util().deserialize(body, DocumentUpdateEntity.class);
                final VPackSlice newDoc = body.get(NEW);
                if (newDoc.isObject()) {
                    doc.setNew((T) util(Serializer.CUSTOM).deserialize(newDoc, value.getClass()));
                }
                final VPackSlice oldDoc = body.get(OLD);
                if (oldDoc.isObject()) {
                    doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, value.getClass()));
                }
                if (options == null || Boolean.TRUE != options.getSilent()) {
                    final Map<DocumentField.Type, String> values = new HashMap<DocumentField.Type, String>();
                    values.put(DocumentField.Type.REV, doc.getRev());
                    executor.documentCache().setValues(value, values);
                }
                return doc;
            }
        };
    }

    protected <T> Request updateDocumentsRequest(final Collection<T> values, final DocumentUpdateOptions params) {
        final Request request = request(db.tenant(), db.name(), RequestType.PATCH, PATH_API_DOCUMENT, name);
        final Boolean keepNull = params.getKeepNull();
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.KEEP_NULL, keepNull);
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(MERGE_OBJECTS, params.getMergeObjects());
        request.putQueryParam(IGNORE_REVS, params.getIgnoreRevs());
        request.putQueryParam(RETURN_NEW, params.getReturnNew());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util(Serializer.CUSTOM).serialize(values,
                new C8Serializer.Options()
                        .serializeNullValues(params.getSerializeNull() == null || params.getSerializeNull())
                        .stringAsJson(true)));
        return request;
    }

    @SuppressWarnings("unchecked")
    protected <T> ResponseDeserializer<MultiDocumentEntity<DocumentUpdateEntity<T>>> updateDocumentsResponseDeserializer(
            final Collection<T> values, final DocumentUpdateOptions params) {
        return new ResponseDeserializer<MultiDocumentEntity<DocumentUpdateEntity<T>>>() {
            @Override
            public MultiDocumentEntity<DocumentUpdateEntity<T>> deserialize(final Response response)
                    throws VPackException {
                Class<T> type = null;
                if (Boolean.TRUE == params.getReturnNew() || Boolean.TRUE == params.getReturnOld()) {
                    if (!values.isEmpty()) {
                        type = (Class<T>) values.iterator().next().getClass();
                    }
                }
                final MultiDocumentEntity<DocumentUpdateEntity<T>> multiDocument = new MultiDocumentEntity<DocumentUpdateEntity<T>>();
                final Collection<DocumentUpdateEntity<T>> docs = new ArrayList<DocumentUpdateEntity<T>>();
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
                            final DocumentUpdateEntity<T> doc = util().deserialize(next, DocumentUpdateEntity.class);
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
            }
        };
    }

    protected Request deleteDocumentRequest(final String key, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_DOCUMENT,
                DocumentUtil.createDocumentHandle(name, key));
        final DocumentDeleteOptions params = (options != null ? options : new DocumentDeleteOptions());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        return request;
    }

    protected <T> ResponseDeserializer<DocumentDeleteEntity<T>> deleteDocumentResponseDeserializer(
            final Class<T> type) {
        return new ResponseDeserializer<DocumentDeleteEntity<T>>() {
            @SuppressWarnings("unchecked")
            @Override
            public DocumentDeleteEntity<T> deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                final DocumentDeleteEntity<T> doc = util().deserialize(body, DocumentDeleteEntity.class);
                final VPackSlice oldDoc = body.get(OLD);
                if (oldDoc.isObject()) {
                    doc.setOld((T) util(Serializer.CUSTOM).deserialize(oldDoc, type));
                }
                return doc;
            }
        };
    }

    protected <T> Request deleteDocumentsRequest(final Collection<T> keys, final DocumentDeleteOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_DOCUMENT, name);
        final DocumentDeleteOptions params = (options != null ? options : new DocumentDeleteOptions());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putQueryParam(C8RequestParam.WAIT_FOR_SYNC, params.getWaitForSync());
        request.putQueryParam(RETURN_OLD, params.getReturnOld());
        request.putQueryParam(SILENT, params.getSilent());
        request.setBody(util().serialize(keys));
        return request;
    }

    protected <T> ResponseDeserializer<MultiDocumentEntity<DocumentDeleteEntity<T>>> deleteDocumentsResponseDeserializer(
            final Class<T> type) {
        return new ResponseDeserializer<MultiDocumentEntity<DocumentDeleteEntity<T>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public MultiDocumentEntity<DocumentDeleteEntity<T>> deserialize(final Response response)
                    throws VPackException {
                final MultiDocumentEntity<DocumentDeleteEntity<T>> multiDocument = new MultiDocumentEntity<DocumentDeleteEntity<T>>();
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
            }
        };
    }

    protected Request documentExistsRequest(final String key, final DocumentExistsOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.HEAD, PATH_API_DOCUMENT,
                DocumentUtil.createDocumentHandle(name, key));
        final DocumentExistsOptions params = (options != null ? options : new DocumentExistsOptions());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        request.putHeaderParam(C8RequestParam.IF_MATCH, params.getIfMatch());
        request.putHeaderParam(C8RequestParam.IF_NONE_MATCH, params.getIfNoneMatch());
        return request;
    }

    protected Request getIndexRequest(final String id) {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_INDEX, createIndexId(id));
    }

    protected Request deleteIndexRequest(final String id) {
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_INDEX, createIndexId(id));
    }

    protected ResponseDeserializer<String> deleteIndexResponseDeserializer() {
        return new ResponseDeserializer<String>() {
            @Override
            public String deserialize(final Response response) throws VPackException {
                return response.getBody().get("id").getAsString();
            }
        };
    }

    private String createIndexId(final String id) {
        final String index;
        if (id.matches(DocumentUtil.REGEX_ID)) {
            index = id;
        } else if (id.matches(DocumentUtil.REGEX_KEY)) {
            index = name + "/" + id;
        } else {
            throw new C8DBException(String.format("index id %s is not valid.", id));
        }
        return index;
    }

    protected Request createHashIndexRequest(final Iterable<String> fields, final HashIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new HashIndexOptions(), fields)));
        return request;
    }

    protected Request createSkiplistIndexRequest(final Iterable<String> fields, final SkiplistIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new SkiplistIndexOptions(), fields)));
        return request;
    }

    protected Request createPersistentIndexRequest(final Iterable<String> fields,
            final PersistentIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(util()
                .serialize(OptionsBuilder.build(options != null ? options : new PersistentIndexOptions(), fields)));
        return request;
    }

    protected Request createGeoIndexRequest(final Iterable<String> fields, final GeoIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new GeoIndexOptions(), fields)));
        return request;
    }

    protected Request createFulltextIndexRequest(final Iterable<String> fields, final FulltextIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new FulltextIndexOptions(), fields)));
        return request;
    }

    // Macrometa Corp Modification: Add `createTTLIndexRequest` method.
    protected Request createTTLIndexRequest(final Iterable<String> fields, final TTLIndexOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.POST, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        request.setBody(
                util().serialize(OptionsBuilder.build(options != null ? options : new TTLIndexOptions(), fields)));
        return request;
    }

    protected Request getIndexesRequest() {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_INDEX);
        request.putQueryParam(COLLECTION_QUERY_PARAM, name);
        return request;
    }

    protected ResponseDeserializer<Collection<IndexEntity>> getIndexesResponseDeserializer() {
        return new ResponseDeserializer<Collection<IndexEntity>>() {
            @Override
            public Collection<IndexEntity> deserialize(final Response response) throws VPackException {
                return util().deserialize(response.getBody().get("indexes"), new Type<Collection<IndexEntity>>() {
                }.getType());
            }
        };
    }

    protected Request truncateRequest(final CollectionTruncateOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_COLLECTION, name, "truncate");
        final CollectionTruncateOptions params = (options != null ? options : new CollectionTruncateOptions());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        return request;
    }

    protected Request countRequest(final CollectionCountOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.GET, PATH_API_COLLECTION, name, "count");
        final CollectionCountOptions params = (options != null ? options : new CollectionCountOptions());
        request.putHeaderParam(TRANSACTION_ID, params.getStreamTransactionId());
        return request;
    }

    protected Request dropRequest(final Boolean isSystem) {
        return request(db.tenant(), db.name(), RequestType.DELETE, PATH_API_COLLECTION, name).putQueryParam("isSystem",
                isSystem);
    }

    protected Request getInfoRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_COLLECTION, name);
    }

    protected Request getPropertiesRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_COLLECTION, name, "properties");
    }

    protected Request changePropertiesRequest(final CollectionPropertiesOptions options) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_COLLECTION, name,
                "properties");
        request.setBody(util().serialize(options != null ? options : new CollectionPropertiesOptions()));
        return request;
    }

    protected Request renameRequest(final String newName) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_COLLECTION, name, "rename");
        request.setBody(util().serialize(OptionsBuilder.build(new CollectionRenameOptions(), newName)));
        return request;
    }

    protected <T> Request responsibleShardRequest(final T value) {
        final Request request = request(db.tenant(), db.name(), RequestType.PUT, PATH_API_COLLECTION, name,
                "responsibleShard");
        request.setBody(util(Serializer.CUSTOM).serialize(value));
        return request;
    }

    protected Request getRevisionRequest() {
        return request(db.tenant(), db.name(), RequestType.GET, PATH_API_COLLECTION, name, "revision");
    }

    protected Request grantAccessRequest(final String user, final Permissions permissions) {
        return request(null, C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, String.join("." , db.tenant(), user),
                C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()), C8RequestParam.COLLECTION, name)
                        .setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
    }

    protected Request resetAccessRequest(final String user) {
        return request(null, C8RequestParam.SYSTEM, RequestType.DELETE, PATH_API_USER, String.join("." , db.tenant(), user),
                C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()), C8RequestParam.COLLECTION, name);
    }

    protected Request getPermissionsRequest(final String user) {
        return request(null, C8RequestParam.SYSTEM, RequestType.GET, PATH_API_USER, String.join("." , db.tenant(), user),
                C8RequestParam.DATABASE, String.join("." ,db.tenant(), db.name()), C8RequestParam.COLLECTION, name);
    }

    protected ResponseDeserializer<Permissions> getPermissionsResponseDeserialzer() {
        return new ResponseDeserializer<Permissions>() {
            @Override
            public Permissions deserialize(final Response response) throws VPackException {
                final VPackSlice body = response.getBody();
                if (body != null) {
                    final VPackSlice result = body.get(C8ResponseField.RESULT);
                    if (!result.isNone()) {
                        return util().deserialize(result, Permissions.class);
                    }
                }
                return null;
            }
        };
    }

}
