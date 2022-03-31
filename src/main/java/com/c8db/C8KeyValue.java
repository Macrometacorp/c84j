/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;


import com.c8db.entity.C8KVEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.C8KVPairReadOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.DocumentCreateOptions;

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
     * @param expiration Flag for expiration of the values
     * @param options Additional options, can be null*
     * @return The KV name
     * @throws C8DBException
     */
    C8KVEntity create(final Boolean expiration, CollectionCreateOptions options) throws C8DBException;


    /**
     * Deletes the KV from the database.
     *
     * @throws C8DBException
     */
    void drop() throws C8DBException;


    /**
     * Removes all pair from the KV
     *
     * @return information about the KV
     * @throws C8DBException
     */
    C8KVEntity truncate() throws C8DBException;

    /**
     * Set one or more key-value pairs in key-value collection.
     * If the input is an array of objects then key-value pairs are created in batch.
     * If the key does not exist the key-value pairs are created. Otherwise the entry for the key is updated.
     * Specify expiration in UTC timestamp.
     *
     * @param values  A collection of KV pairs
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertKVPairs(Collection<T> values, DocumentCreateOptions options)
            throws C8DBException;

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
    <T> T getKVPair(String key) throws C8DBException;

    /**
     * Retrieves multiple pairs with the given {@code _key} from the KV.
     *
     * @param keys The keys of the pairs
     * @param options The read options - offset, limit, order
     * @return the documents and possible errors
     * @throws C8DBException
     */
   <T> MultiDocumentEntity<T> getKVPairs(Collection<String> keys, C8KVPairReadOptions options) throws C8DBException;
}
