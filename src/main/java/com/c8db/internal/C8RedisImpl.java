/*
 *  Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.internal;

import com.c8db.C8Collection;
import com.c8db.C8DBException;
import com.c8db.C8Redis;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.CollectionPropertiesEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.entity.DocumentUpdateEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.entity.Permissions;
import com.c8db.model.C8RedisCreateOptions;
import com.c8db.model.CollectionCountOptions;
import com.c8db.model.CollectionDropOptions;
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
import com.c8db.model.TTLIndexOptions;

import java.util.Collection;

public class C8RedisImpl extends InternalC8Redis<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Redis {

    private C8Collection collection;

    protected C8RedisImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
        this.collection = db().collection(tableName);
    }

    @Override
    public CollectionEntity create(C8RedisCreateOptions options) throws C8DBException {
        return executor.execute(createTableRequest(tableName, options), CollectionEntity.class);
    }

    @Override
    public void drop() throws C8DBException {
        collection.drop();
    }

    @Override
    public void drop(CollectionDropOptions options) throws C8DBException {
        collection.drop(options);
    }

    @Override
    public CollectionEntity getInfo() throws C8DBException {
        return collection.getInfo();
    }

    @Override
    public CollectionPropertiesEntity getProperties() throws C8DBException {
        return collection.getProperties();
    }

    @Override
    public CollectionPropertiesEntity changeProperties(CollectionPropertiesOptions options) throws C8DBException {
        return collection.changeProperties(options);
    }

    @Override
    public CollectionEntity rename(String newName) throws C8DBException {
        return collection.rename(newName);
    }

    @Override
    public void grantAccess(String user, Permissions permissions) throws C8DBException {
        collection.grantAccess(user, permissions);
    }

    @Override
    public void revokeAccess(String user) throws C8DBException {
        collection.revokeAccess(user);
    }

    @Override
    public void resetAccess(String user) throws C8DBException {
        collection.resetAccess(user);
    }

    @Override
    public Permissions getPermissions(String user) throws C8DBException {
        return collection.getPermissions(user);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(final Collection<T> values)
        throws C8DBException {
        return collection.insertDocuments(values, new DocumentCreateOptions());
    }

    @Override
    public <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(Collection<T> values, DocumentCreateOptions options) throws C8DBException {
        return collection.insertDocuments(values, options);
    }

    @Override
    public String name() {
        return tableName;
    }

    @Override
    public <T> DocumentCreateEntity<T> insertDocument(T value) throws C8DBException {
        return collection.insertDocument(value);
    }

    @Override
    public <T> DocumentCreateEntity<T> insertDocument(T value, DocumentCreateOptions options) throws C8DBException {
        return collection.insertDocument(value, options);
    }

    @Override
    public <T> T getDocument(String key, Class<T> type) throws C8DBException {
        return collection.getDocument(key, type);
    }

    @Override
    public <T> T getDocument(String key, Class<T> type, DocumentReadOptions options) throws C8DBException {
        return collection.getDocument(key, type, options);
    }

    @Override
    public <T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type) throws C8DBException {
        return collection.getDocuments(keys, type);
    }

    @Override
    public <T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type, DocumentReadOptions options) throws C8DBException {
        return collection.getDocuments(keys, type, options);
    }

    @Override
    public <T> DocumentUpdateEntity<T> replaceDocument(String key, T value) throws C8DBException {
        return collection.replaceDocument(key, value);
    }

    @Override
    public <T> DocumentUpdateEntity<T> replaceDocument(String key, T value, DocumentReplaceOptions options) throws C8DBException {
        return collection.replaceDocument(key, value, options);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(Collection<T> values) throws C8DBException {
        return collection.replaceDocuments(values);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(Collection<T> values, DocumentReplaceOptions options) throws C8DBException {
        return collection.replaceDocuments(values, options);
    }

    @Override
    public <T> DocumentUpdateEntity<T> updateDocument(String key, T value) throws C8DBException {
        return collection.updateDocument(key, value);
    }

    @Override
    public <T> DocumentUpdateEntity<T> updateDocument(String key, T value, DocumentUpdateOptions options) throws C8DBException {
        return collection.updateDocument(key, value, options);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(Collection<T> values) throws C8DBException {
        return collection.updateDocuments(values);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(Collection<T> values, DocumentUpdateOptions options) throws C8DBException {
        return collection.updateDocuments(values, options);
    }

    @Override
    public DocumentDeleteEntity<Void> deleteDocument(String key) throws C8DBException {
        return collection.deleteDocument(key);
    }

    @Override
    public <T> DocumentDeleteEntity<T> deleteDocument(String key, Class<T> type, DocumentDeleteOptions options) throws C8DBException {
        return collection.deleteDocument(key, type, options);
    }

    @Override
    public MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteDocuments(Collection<?> values) throws C8DBException {
        return collection.deleteDocuments(values);
    }

    @Override
    public <T> MultiDocumentEntity<DocumentDeleteEntity<T>> deleteDocuments(Collection<?> values, Class<T> type, DocumentDeleteOptions options) throws C8DBException {
        return collection.deleteDocuments(values, type, options);
    }

    @Override
    public Boolean documentExists(String key) {
        return collection.documentExists(key);
    }

    @Override
    public Boolean documentExists(String key, DocumentExistsOptions options) throws C8DBException {
        return collection.documentExists(key, options);
    }

    @Override
    public IndexEntity getIndex(String id) throws C8DBException {
        return null;
    }

    @Override
    public Collection<IndexEntity> getIndexes() throws C8DBException {
        return collection.getIndexes();
    }

    @Override
    public boolean exists() throws C8DBException {
        return collection.exists();
    }

    @Override
    public CollectionEntity truncate() throws C8DBException {
        return collection.truncate();
    }

    @Override
    public CollectionEntity truncate(CollectionTruncateOptions options) throws C8DBException {
        return collection.truncate(options);
    }

    @Override
    public CollectionPropertiesEntity count() throws C8DBException {
        return collection.count();
    }

    @Override
    public CollectionPropertiesEntity count(CollectionCountOptions options) throws C8DBException {
        return collection.count(options);
    }
}
