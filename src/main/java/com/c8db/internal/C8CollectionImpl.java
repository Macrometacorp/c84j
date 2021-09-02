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

import java.util.Collection;

import com.c8db.model.TTLIndexOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8Collection;
import com.c8db.C8DBException;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.CollectionPropertiesEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.entity.DocumentUpdateEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.entity.Permissions;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.model.CollectionCountOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.CollectionPropertiesOptions;
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
import com.c8db.model.PersistentIndexOptions;
import com.c8db.model.SkiplistIndexOptions;

/**
 */
public class C8CollectionImpl extends InternalC8Collection<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Collection {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8Collection.class);

    protected C8CollectionImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public boolean exists() throws C8DBException {
        try {
            getInfo();
            return true;
        } catch (final C8DBException e) {
            if (C8Errors.ERROR_C8_DATA_SOURCE_NOT_FOUND.equals(e.getErrorNum())) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public CollectionEntity truncate() throws C8DBException {
        return truncate(null);
    }

    @Override
    public CollectionEntity truncate(CollectionTruncateOptions options) throws C8DBException {
        return executor.execute(truncateRequest(options), CollectionEntity.class);
    }

    @Override
    public CollectionPropertiesEntity count() throws C8DBException {
        return count(null);
    }

    @Override
    public CollectionPropertiesEntity count(CollectionCountOptions options) throws C8DBException {
        return executor.execute(countRequest(options), CollectionPropertiesEntity.class);
    }

    @Override
    public CollectionEntity create() throws C8DBException {
        return db().createCollection(name());
    }

    @Override
    public CollectionEntity create(final CollectionCreateOptions options) throws C8DBException {
        return db().createCollection(name(), options);
    }

    @Override
    public void drop() throws C8DBException {
        executor.execute(dropRequest(null), Void.class);
    }

    @Override
    public void drop(final boolean isSystem) throws C8DBException {
        executor.execute(dropRequest(isSystem), Void.class);
    }

    @Override
    public CollectionEntity getInfo() throws C8DBException {
        return executor.execute(getInfoRequest(), CollectionEntity.class);
    }

    @Override
    public CollectionPropertiesEntity getProperties() throws C8DBException {
        return executor.execute(getPropertiesRequest(), CollectionPropertiesEntity.class);
    }

    @Override
    public CollectionPropertiesEntity changeProperties(final CollectionPropertiesOptions options)
            throws C8DBException {
        return executor.execute(changePropertiesRequest(options), CollectionPropertiesEntity.class);
    }

    @Override
    public synchronized CollectionEntity rename(final String newName) throws C8DBException {
        final CollectionEntity result = executor.execute(renameRequest(newName), CollectionEntity.class);
        name = result.getName();
        return result;
    }

    /*
     * @Override public ShardEntity getResponsibleShard(final Object value) throws
     * ArangoDBException { return executor.execute(responsibleShardRequest(value),
     * ShardEntity.class); }
     * 
     * @Override public CollectionRevisionEntity getRevision() throws
     * ArangoDBException { return executor.execute(getRevisionRequest(),
     * CollectionRevisionEntity.class); }
     */
    @Override
    public void grantAccess(final String user, final Permissions permissions) throws C8DBException {
        executor.execute(grantAccessRequest(user, permissions), Void.class);
    }

    @Override
    public void revokeAccess(final String user) throws C8DBException {
        executor.execute(grantAccessRequest(user, Permissions.NONE), Void.class);
    }

    @Override
    public void resetAccess(final String user) throws C8DBException {
        executor.execute(resetAccessRequest(user), Void.class);
    }

    @Override
    public Permissions getPermissions(final String user) throws C8DBException {
        return executor.execute(getPermissionsRequest(user), getPermissionsResponseDeserialzer());
    }

    @Override
    public <T> DocumentCreateEntity<T> insertDocument(final T value) throws C8DBException {
        return insertDocument(value, new DocumentCreateOptions());
    }

    @Override
    public <T> DocumentCreateEntity<T> insertDocument(final T value, final DocumentCreateOptions options)
            throws C8DBException {
        return executor.execute(insertDocumentRequest(value, options),
                insertDocumentResponseDeserializer(value, options));
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(final Collection<T> values)
            throws C8DBException {
        return insertDocuments(values, new DocumentCreateOptions());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(final Collection<T> values,
            final DocumentCreateOptions options) throws C8DBException {
        final DocumentCreateOptions params = (options != null ? options : new DocumentCreateOptions());
        return executor.execute(insertDocumentsRequest(values, params),
                insertDocumentsResponseDeserializer(values, params));
    }

    @Override
    public <T> T getDocument(final String key, final Class<T> type) throws C8DBException {
        return getDocument(key, type, new DocumentReadOptions());
    }

    @Override
    public <T> T getDocument(final String key, final Class<T> type, final DocumentReadOptions options)
            throws C8DBException {
        DocumentUtil.validateDocumentKey(key);
        try {
            return executor.execute(getDocumentRequest(key, options), type);
        } catch (final C8DBException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            }

            // handle Response: 404, Error: 1655 - transaction not found
            if (e.getErrorNum() != null && e.getErrorNum() == 1655) {
                throw e;
            }

            if ((e.getResponseCode() != null
                    && (e.getResponseCode() == 404 || e.getResponseCode() == 304 || e.getResponseCode() == 412))
                    && (options == null || options.isCatchException())) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public <T> MultiDocumentEntity<T> getDocuments(final Collection<String> keys, final Class<T> type)
            throws C8DBException {
        return getDocuments(keys, type, new DocumentReadOptions());
    }

    @Override
    public <T> MultiDocumentEntity<T> getDocuments(final Collection<String> keys, final Class<T> type,
            final DocumentReadOptions options) throws C8DBException {
        return executor.execute(getDocumentsRequest(keys, options), getDocumentsResponseDeserializer(type, options));
    }

    @Override
    public <T> DocumentUpdateEntity<T> replaceDocument(final String key, final T value) throws C8DBException {
        return replaceDocument(key, value, new DocumentReplaceOptions());
    }

    @Override
    public <T> DocumentUpdateEntity<T> replaceDocument(final String key, final T value,
            final DocumentReplaceOptions options) throws C8DBException {
        return executor.execute(replaceDocumentRequest(key, value, options),
                replaceDocumentResponseDeserializer(value, options));
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(final Collection<T> values)
            throws C8DBException {
        return replaceDocuments(values, new DocumentReplaceOptions());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(final Collection<T> values,
            final DocumentReplaceOptions options) throws C8DBException {
        final DocumentReplaceOptions params = (options != null ? options : new DocumentReplaceOptions());
        return executor.execute(replaceDocumentsRequest(values, params),
                replaceDocumentsResponseDeserializer(values, params));
    }

    @Override
    public <T> DocumentUpdateEntity<T> updateDocument(final String key, final T value) throws C8DBException {
        return updateDocument(key, value, new DocumentUpdateOptions());
    }

    @Override
    public <T> DocumentUpdateEntity<T> updateDocument(final String key, final T value,
            final DocumentUpdateOptions options) throws C8DBException {
        return executor.execute(updateDocumentRequest(key, value, options),
                updateDocumentResponseDeserializer(value, options));
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(final Collection<T> values)
            throws C8DBException {
        return updateDocuments(values, new DocumentUpdateOptions());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(final Collection<T> values,
            final DocumentUpdateOptions options) throws C8DBException {
        final DocumentUpdateOptions params = (options != null ? options : new DocumentUpdateOptions());
        return executor.execute(updateDocumentsRequest(values, params),
                updateDocumentsResponseDeserializer(values, params));
    }

    @Override
    public DocumentDeleteEntity<Void> deleteDocument(final String key) throws C8DBException {
        return executor.execute(deleteDocumentRequest(key, new DocumentDeleteOptions()),
                deleteDocumentResponseDeserializer(Void.class));
    }

    @Override
    public <T> DocumentDeleteEntity<T> deleteDocument(final String key, final Class<T> type,
            final DocumentDeleteOptions options) throws C8DBException {
        return executor.execute(deleteDocumentRequest(key, options), deleteDocumentResponseDeserializer(type));
    }

    @Override
    public MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteDocuments(final Collection<?> values)
            throws C8DBException {
        return executor.execute(deleteDocumentsRequest(values, new DocumentDeleteOptions()),
                deleteDocumentsResponseDeserializer(Void.class));
    }

    @Override
    public <T> MultiDocumentEntity<DocumentDeleteEntity<T>> deleteDocuments(final Collection<?> values,
            final Class<T> type, final DocumentDeleteOptions options) throws C8DBException {
        return executor.execute(deleteDocumentsRequest(values, options), deleteDocumentsResponseDeserializer(type));
    }

    @Override
    public Boolean documentExists(final String key) {
        return documentExists(key, new DocumentExistsOptions());
    }

    @Override
    public Boolean documentExists(final String key, final DocumentExistsOptions options) throws C8DBException {
        try {
            executor.execute(documentExistsRequest(key, options), VPackSlice.class);
            return true;
        } catch (final C8DBException e) {

            // handle Response: 404, Error: 1655 - transaction not found
            if (e.getErrorNum() != null && e.getErrorNum() == 1655) {
                throw e;
            }

            if ((e.getResponseCode() != null
                    && (e.getResponseCode() == 404 || e.getResponseCode() == 304 || e.getResponseCode() == 412))
                    && (options == null || options.isCatchException())) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public IndexEntity getIndex(final String id) throws C8DBException {
        return executor.execute(getIndexRequest(id), IndexEntity.class);
    }

    @Override
    public String deleteIndex(final String id) throws C8DBException {
        return executor.execute(deleteIndexRequest(id), deleteIndexResponseDeserializer());
    }

    @Override
    public IndexEntity ensureHashIndex(final Iterable<String> fields, final HashIndexOptions options)
            throws C8DBException {
        return executor.execute(createHashIndexRequest(fields, options), IndexEntity.class);
    }

    @Override
    public IndexEntity ensureSkiplistIndex(final Iterable<String> fields, final SkiplistIndexOptions options)
            throws C8DBException {
        return executor.execute(createSkiplistIndexRequest(fields, options), IndexEntity.class);
    }

    @Override
    public IndexEntity ensurePersistentIndex(final Iterable<String> fields, final PersistentIndexOptions options)
            throws C8DBException {
        return executor.execute(createPersistentIndexRequest(fields, options), IndexEntity.class);
    }

    @Override
    public IndexEntity ensureGeoIndex(final Iterable<String> fields, final GeoIndexOptions options)
            throws C8DBException {
        return executor.execute(createGeoIndexRequest(fields, options), IndexEntity.class);
    }

    @Override
    public IndexEntity ensureFulltextIndex(final Iterable<String> fields, final FulltextIndexOptions options)
            throws C8DBException {
        return executor.execute(createFulltextIndexRequest(fields, options), IndexEntity.class);
    }

    // Macrometa Corp Modification: Add `ensureTTLIndex` method.
    @Override
    public IndexEntity ensureTTLIndex(final Iterable<String> fields, final TTLIndexOptions options)
            throws C8DBException {
        return executor.execute(createTTLIndexRequest(fields, options), IndexEntity.class);
    }

    @Override
    public Collection<IndexEntity> getIndexes() throws C8DBException {
        return executor.execute(getIndexesRequest(), getIndexesResponseDeserializer());
    }


}
