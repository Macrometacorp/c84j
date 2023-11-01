/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.internal;

import com.c8db.C8DBException;
import com.c8db.C8KeyValue;
import com.c8db.entity.*;
import com.c8db.internal.util.DocumentUtil;
import com.c8db.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class C8KeyValueImpl extends InternalC8KeyValue<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8KeyValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8KeyValueImpl.class);

    protected C8KeyValueImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public Collection<C8KVCollectionEntity> all() throws C8DBException {
        return executor.execute(getAllCollections(), getAllCollectionsResponseDeserializer());
    }

    @Override
    public C8KVEntity create(C8KVCreateOptions options) throws C8DBException {
        return executor.execute(createRequest(name, options), C8KVEntity.class);
    }

    @Override
    public C8KVEntity create() throws C8DBException {
        return executor.execute(createRequest(name, null), C8KVEntity.class);
    }

    @Override
    public void drop() throws C8DBException {
        executor.execute(dropRequest(), Void.class);
    }

    @Override
    public void truncate() throws C8DBException {
        truncate(null);
    }

    @Override
    public void truncate(C8KVTruncateOptions options) throws C8DBException {
        executor.execute(truncateRequest(options), Void.class);
    }

    @Override
    public  MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> insertKVPairs(final Collection<BaseKeyValue>  values)
            throws C8DBException {
        return insertKVPairs(values, null);
    }

    @Override
    public  MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> insertKVPairs(final Collection<BaseKeyValue>  values,
                                                                                  C8KVInsertValuesOptions options)
            throws C8DBException {
        return executor.execute(insertKVPairsRequest(values, options), insertKVPairsResponseDeserializer());
    }

    @Override
    public DocumentDeleteEntity<Void> deleteKVPair(String key) throws C8DBException {
        return deleteKVPair(key, null);
    }

    @Override
    public DocumentDeleteEntity<Void> deleteKVPair(String key, C8KVDeleteValueOptions options) throws C8DBException {
        return executor.execute(deleteKVPairRequest(key, options),
                deleteKVPairResponseDeserializer(Void.class));
    }

    @Override
    public MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteKVPairs(Collection<?> values) throws C8DBException {
        return deleteKVPairs(values, null);
    }

    @Override
    public MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteKVPairs(Collection<?> values, C8KVDeleteValuesOptions options) throws C8DBException {
        return executor.execute(deleteKVPairsRequest(values, options), deleteKVPairsResponseDeserializer(Void.class));
    }

    @Override
    public BaseKeyValue getKVPair(String key) throws C8DBException {
        return getKVPair(key, null);
    }

    @Override
    public BaseKeyValue getKVPair(String key, C8KVReadValueOptions options) throws C8DBException {
        DocumentUtil.validateDocumentKey(key);
        try {
            return executor.execute(getKVPairRequest(key, options), BaseKeyValue.class);
        } catch (final C8DBException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            }

            // handle Response: 404, Error: 1655 - transaction not found
            if (e.getErrorNum() != null && e.getErrorNum() == 1655) {
                throw e;
            }

            if ((e.getResponseCode() != null
                    && (e.getResponseCode() == 404 || e.getResponseCode() == 304 || e.getResponseCode() == 412))) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public MultiDocumentEntity<BaseKeyValue> getKVPairs()
            throws C8DBException {
        return getKVPairs(null);
    }

    @Override
    public MultiDocumentEntity<BaseKeyValue> getKVPairs(final C8KVReadValuesOptions options)
            throws C8DBException {
        return executor.execute(getKVPairsRequest(options), getKVPairsResponseDeserializer());
    }

    @Override
    public Collection<String> getKVKeys() throws C8DBException {
        return getKVKeys(null);
    }

    @Override
    public Collection<String> getKVKeys(C8KVReadKeysOptions options) throws C8DBException {
        return executor.execute(getKVKeysRequest(options), getKVKeysResponseDeserializer());
    }

    @Override
    public long countKVPairs(C8KVCountPairsOptions options) throws C8DBException {
        return executor.execute(countKVPairsRequest(options), countKVPairsResponseDeserializer());
    }

    @Override
    public long countKVPairs() throws C8DBException {
        return countKVPairs(null);
    }

    @Override
    public Collection<String> getGroups(C8KVReadGroupsOptions options) throws C8DBException {
        return executor.execute(getAllGroupsRequest(options), getAllGroupsResponseDeserializer());
    }

    @Override
    public Collection<String> getGroups() throws C8DBException {
        return getGroups(null);
    }

    @Override
    public void updateGroup(String oldGroupID, String newGroupID, C8KVUpdateGroupOptions options) throws C8DBException {
        executor.execute(updateGroupRequest(oldGroupID, newGroupID, options), C8KVEntity.class);
    }

    @Override
    public void updateGroup(String oldGroupID, String newGroupID) throws C8DBException {
        updateGroup(oldGroupID, newGroupID, null);
    }
}
