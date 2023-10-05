/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;


import com.c8db.entity.*;
import com.c8db.model.*;

import java.util.Collection;

public interface C8KeyValue {

    /**
     * The handler of the database the collection is within
     *
     * @return database handler
     */
    public C8Database db();

    /**
     * Creates a KV with the given {@code options} for this KV's
     * name, then returns KV name from the server.
     *
     * @param options Additional options, can be null*
     * @return The KV name
     * @throws C8DBException
     */
    C8KVEntity create(C8KVCreateOptions options) throws C8DBException;

    /**
     * Creates a KV with the given {@code options} for this KV's
     * name, then returns KV name from the server.
     *
     * @return The KV name
     * @throws C8DBException
     */
    C8KVEntity create() throws C8DBException;

    /**
     * Deletes the KV from the database.
     *
     * @throws C8DBException
     */
    void drop() throws C8DBException;


    /**
     * Removes all pair from the KV
     *
     * @throws C8DBException
     */
    void truncate() throws C8DBException;

    /**
     * Removes all pair from the KV
     *
     * @param options The read options - group
     * @throws C8DBException
     */
    void truncate(C8KVTruncateOptions options) throws C8DBException;

    /**
     * Set one or more key-value pairs in key-value collection.
     * If the input is an array of objects then key-value pairs are created in batch.
     * If the key does not exist the key-value pairs are created. Otherwise the entry for the key is updated.
     * Specify expiration in UTC timestamp.
     *
     * @param values  A collection of KV pairs
     * @return information about the document
     * @throws C8DBException
     */
    MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> insertKVPairs(Collection<BaseKeyValue> values) throws C8DBException;

    /**
     * Deletes a pair with the given {@code key} from the KV.
     *
     * @param key The key of the pair
     * @return information about the pair
     * @throws C8DBException
     */
    DocumentDeleteEntity<Void> deleteKVPair(String key) throws C8DBException;

    /**
     * Deletes multiple documents from the KV.
     *
     * @param values The keys of the pairs or the KVs themselves
     * @return information about the pair
     * @throws C8DBException
     */
    MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteKVPairs(Collection<?> values) throws C8DBException;

    /**
     * Retrieves the KV pair with the given {@code key} from the KV.
     *
     * @param key     The key of the pair
     * @return the document identified by the key
     */
    BaseKeyValue getKVPair(String key) throws C8DBException;

    /**
     * Retrieve all KV collections.
     *
     * @return list of KV collections
     * @throws C8DBException
     */
    Collection<C8KVCollectionEntity> all() throws C8DBException;

    /**
     * Retrieves multiple pairs.
     *
     * @return the documents and possible errors
     * @throws C8DBException
     */
    MultiDocumentEntity<BaseKeyValue> getKVPairs() throws C8DBException;

    /**
     * Retrieves multiple pairs with the given {@code _key} from the KV.
     *
     * @param options The read options - offset, limit, order
     * @return the documents and possible errors
     * @throws C8DBException
     */
   MultiDocumentEntity<BaseKeyValue> getKVPairs(C8KVReadValuesOptions options) throws C8DBException;

    /**
     * Retrieves keys from key-value collection.
     *
     * @return
     * @throws C8DBException
     */
    Collection<String> getKVKeys() throws C8DBException;

    /**
     * Retrieves keys from key-value collection.
     *
     * @param options
     * @return
     * @throws C8DBException
     */
    Collection<String> getKVKeys(C8KVReadKeysOptions options) throws C8DBException;

    /**
     * Retrieves number of key-value pairs in collection.
     *
     * @param options ot set parameter group
     * @return number of key-value pairs in collection.
     * @throws C8DBException
     */
    long countKVPairs(C8KVCountPairsOptions options) throws C8DBException;

    /**
     * Retrieves number of key-value pairs in collection.
     *
     * @return number of key-value pairs in collection.
     * @throws C8DBException
     */
    long countKVPairs() throws C8DBException;
}
