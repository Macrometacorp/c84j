/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db;

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
import com.c8db.model.CollectionPropertiesOptions;
import com.c8db.model.CollectionTruncateOptions;
import com.c8db.model.DocumentCreateOptions;
import com.c8db.model.DocumentDeleteOptions;
import com.c8db.model.DocumentExistsOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.DocumentReplaceOptions;
import com.c8db.model.DocumentUpdateOptions;

import java.util.Collection;

public interface C8Redis {

    /**
     * The the handler of the database the collection is within
     *
     * @return database handler
     */
    C8Database db();

    /**
     * The name of the collection
     *
     * @return collection name
     */
    String name();

    /**
     * Creates a new document from the given document, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param value A representation of a single document (POJO, VPackSlice or
     *              String for JSON)
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
     *      Documentation</a>
     */
    <T> DocumentCreateEntity<T> insertDocument(T value) throws C8DBException;

    /**
     * Creates a new document from the given document, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param value   A representation of a single document (POJO, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
     *      Documentation</a>
     */
    <T> DocumentCreateEntity<T> insertDocument(T value, DocumentCreateOptions options) throws C8DBException;

    /**
     * Creates new documents from the given documents, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param values A List of documents (POJO, VPackSlice or String for JSON)
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(Collection<T> values) throws C8DBException;

    /**
     * Creates new documents from the given documents, unless there is already a
     * document with the _key given. If no _key is given, a new unique _key is
     * generated automatically.
     *
     * @param values  A List of documents (POJO, VPackSlice or String for JSON)
     * @param options Additional options, can be null
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(Collection<T> values,
                                                                     DocumentCreateOptions options) throws C8DBException;

    /**
     * Retrieves the document with the given {@code key} from the collection.
     *
     * @param key  The key of the document
     * @param type The type of the document (POJO class, VPackSlice or String for
     *             JSON)
     * @return the document identified by the key
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
     *      Documentation</a>
     */
    <T> T getDocument(String key, Class<T> type) throws C8DBException;

    /**
     * Retrieves the document with the given {@code key} from the collection.
     *
     * @param key     The key of the document
     * @param type    The type of the document (POJO class, VPackSlice or String for
     *                JSON)
     * @param options Additional options, can be null
     * @return the document identified by the key
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
     *      Documentation</a>
     */
    <T> T getDocument(String key, Class<T> type, DocumentReadOptions options) throws C8DBException;

    /**
     * Retrieves multiple documents with the given {@code _key} from the collection.
     *
     * @param keys The keys of the documents
     * @param type The type of the documents (POJO class, VPackSlice or String for
     *             JSON)
     * @return the documents and possible errors
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type) throws C8DBException;

    /**
     * Retrieves multiple documents with the given {@code _key} from the collection.
     *
     * @param keys    The keys of the documents
     * @param type    The type of the documents (POJO class, VPackSlice or String
     *                for JSON)
     * @param options Additional options, can be null
     * @return the documents and possible errors
     * @throws C8DBException
     */
    <T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type, DocumentReadOptions options)
        throws C8DBException;

    /**
     * Replaces the document with {@code key} with the one in the body, provided
     * there is such a document and no precondition is violated
     *
     * @param key   The key of the document
     * @param value A representation of a single document (POJO, VPackSlice or
     *              String for JSON)
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#replace-document">API
     *      Documentation</a>
     */
    <T> DocumentUpdateEntity<T> replaceDocument(String key, T value) throws C8DBException;

    /**
     * Replaces the document with {@code key} with the one in the body, provided
     * there is such a document and no precondition is violated
     *
     * @param key     The key of the document
     * @param value   A representation of a single document (POJO, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#replace-document">API
     *      Documentation</a>
     */
    <T> DocumentUpdateEntity<T> replaceDocument(String key, T value, DocumentReplaceOptions options)
        throws C8DBException;

    /**
     * Replaces multiple documents in the specified collection with the ones in the
     * values, the replaced documents are specified by the _key attributes in the
     * documents in values.
     *
     * @param values A List of documents (POJO, VPackSlice or String for JSON)
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#replace-documents">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(Collection<T> values) throws C8DBException;

    /**
     * Replaces multiple documents in the specified collection with the ones in the
     * values, the replaced documents are specified by the _key attributes in the
     * documents in values.
     *
     * @param values  A List of documents (POJO, VPackSlice or String for JSON)
     * @param options Additional options, can be null
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#replace-documents">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(Collection<T> values,
                                                                      DocumentReplaceOptions options) throws C8DBException;

    /**
     * Partially updates the document identified by document-key. The value must
     * contain a document with the attributes to patch (the patch document). All
     * attributes from the patch document will be added to the existing document if
     * they do not yet exist, and overwritten in the existing document if they do
     * exist there.
     *
     * @param key   The key of the document
     * @param value A representation of a single document (POJO, VPackSlice or
     *              String for JSON)
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#update-document">API
     *      Documentation</a>
     */
    <T> DocumentUpdateEntity<T> updateDocument(String key, T value) throws C8DBException;

    /**
     * Partially updates the document identified by document-key. The value must
     * contain a document with the attributes to patch (the patch document). All
     * attributes from the patch document will be added to the existing document if
     * they do not yet exist, and overwritten in the existing document if they do
     * exist there.
     *
     * @param key     The key of the document
     * @param value   A representation of a single document (POJO, VPackSlice or
     *                String for JSON)
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#update-document">API
     *      Documentation</a>
     */
    <T> DocumentUpdateEntity<T> updateDocument(String key, T value, DocumentUpdateOptions options)
        throws C8DBException;

    /**
     * Partially updates documents, the documents to update are specified by the
     * _key attributes in the objects on values. Vales must contain a list of
     * document updates with the attributes to patch (the patch documents). All
     * attributes from the patch documents will be added to the existing documents
     * if they do not yet exist, and overwritten in the existing documents if they
     * do exist there.
     *
     * @param values A list of documents (POJO, VPackSlice or String for JSON)
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#update-documents">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(Collection<T> values) throws C8DBException;

    /**
     * Partially updates documents, the documents to update are specified by the
     * _key attributes in the objects on values. Vales must contain a list of
     * document updates with the attributes to patch (the patch documents). All
     * attributes from the patch documents will be added to the existing documents
     * if they do not yet exist, and overwritten in the existing documents if they
     * do exist there.
     *
     * @param values  A list of documents (POJO, VPackSlice or String for JSON)
     * @param options Additional options, can be null
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#update-documents">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(Collection<T> values,
                                                                     DocumentUpdateOptions options) throws C8DBException;

    /**
     * Deletes the document with the given {@code key} from the collection.
     *
     * @param key The key of the document
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#removes-a-document">API
     *      Documentation</a>
     */
    DocumentDeleteEntity<Void> deleteDocument(String key) throws C8DBException;

    /**
     * Deletes the document with the given {@code key} from the collection.
     *
     * @param key     The key of the document
     * @param type    The type of the document (POJO class, VPackSlice or String for
     *                JSON). Only necessary if options.returnOld is set to true,
     *                otherwise can be null.
     * @param options Additional options, can be null
     * @return information about the document
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#removes-a-document">API
     *      Documentation</a>
     */
    <T> DocumentDeleteEntity<T> deleteDocument(String key, Class<T> type, DocumentDeleteOptions options)
        throws C8DBException;

    /**
     * Deletes multiple documents from the collection.
     *
     * @param values The keys of the documents or the documents themselves
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#removes-multiple-documents">API
     *      Documentation</a>
     */
    MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteDocuments(Collection<?> values) throws C8DBException;

    /**
     * Deletes multiple documents from the collection.
     *
     * @param values  The keys of the documents or the documents themselves
     * @param type    The type of the documents (POJO class, VPackSlice or String
     *                for JSON). Only necessary if options.returnOld is set to true,
     *                otherwise can be null.
     * @param options Additional options, can be null
     * @return information about the documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#removes-multiple-documents">API
     *      Documentation</a>
     */
    <T> MultiDocumentEntity<DocumentDeleteEntity<T>> deleteDocuments(Collection<?> values, Class<T> type,
                                                                     DocumentDeleteOptions options) throws C8DBException;

    /**
     * Checks if the document exists by reading a single document head
     *
     * @param key The key of the document
     * @return true if the document was found, otherwise false
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#read-document-header">API
     *      Documentation</a>
     */
    Boolean documentExists(String key);

    /**
     * Checks if the document exists by reading a single document head
     *
     * @param key     The key of the document
     * @param options Additional options, can be null
     * @return true if the document was found, otherwise false
     * @throws C8DBException only thrown when
     *                           {@link DocumentExistsOptions#isCatchException()} ==
     *                           false
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Document/WorkingWithDocuments.html#read-document-header">API
     *      Documentation</a>
     */
    Boolean documentExists(String key, DocumentExistsOptions options) throws C8DBException;

    /**
     * Fetches information about the index with the given {@code id} and returns it.
     *
     * @param id The index-handle
     * @return information about the index
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Indexes/WorkingWith.html#read-index">API
     *      Documentation</a>
     */
    IndexEntity getIndex(String id) throws C8DBException;

    /**
     * Fetches a list of all indexes on this collection.
     *
     * @return information about the indexes
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Indexes/WorkingWith.html#read-all-indexes-of-a-collection">API
     *      Documentation</a>
     */
    Collection<IndexEntity> getIndexes() throws C8DBException;

    /**
     * Checks whether the collection exists
     *
     * @return true if the collection exists, otherwise false
     */
    boolean exists() throws C8DBException;

    /**
     * Removes all documents from the collection, but leaves the indexes intact
     *
     * @return information about the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Creating.html#truncate-collection">API
     *      Documentation</a>
     */
    CollectionEntity truncate() throws C8DBException;

    /**
     * Removes all documents from the collection, but leaves the indexes intact
     *
     * @param options
     * @return information about the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Creating.html#truncate-collection">API
     *      Documentation</a>
     * @since ArangoDB 3.5.0
     */
    CollectionEntity truncate(CollectionTruncateOptions options) throws C8DBException;

    /**
     * Counts the documents in a collection
     *
     * @return information about the collection, including the number of documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Getting.html#return-number-of-documents-in-a-collection">API
     *      Documentation</a>
     */
    CollectionPropertiesEntity count() throws C8DBException;

    /**
     * Counts the documents in a collection
     *
     * @param options
     * @return information about the collection, including the number of documents
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Getting.html#return-number-of-documents-in-a-collection">API
     *      Documentation</a>
     * @since ArangoDB 3.5.0
     */
    CollectionPropertiesEntity count(CollectionCountOptions options) throws C8DBException;

    /**
     * Creates a collection with the given {@code options} for this collection's
     * name, then returns collection information from the server.
     *
     * @param options Additional options, can be null
     * @return information about the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Creating.html#create-collection">API
     *      Documentation</a>
     */
    CollectionEntity create(C8RedisCreateOptions options) throws C8DBException;

    /**
     * Deletes the collection from the database.
     *
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Creating.html#drops-collection">API
     *      Documentation</a>
     */
    void drop() throws C8DBException;

    /**
     * Deletes the collection from the database.
     *
     * @param isSystem Whether or not the collection to drop is a system collection.
     *                 This parameter must be set to true in order to drop a system
     *                 collection.
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Creating.html#drops-collection">API
     *      Documentation</a>
     * @since ArangoDB 3.1.0
     */
    void drop(boolean isSystem) throws C8DBException;

    /**
     * Returns information about the collection
     *
     * @return information about the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Getting.html#return-information-about-a-collection">API
     *      Documentation</a>
     */
    CollectionEntity getInfo() throws C8DBException;

    /**
     * Reads the properties of the specified collection
     *
     * @return properties of the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Getting.html#read-properties-of-a-collection">API
     *      Documentation</a>
     */
    CollectionPropertiesEntity getProperties() throws C8DBException;

    /**
     * Changes the properties of the collection
     *
     * @param options Additional options, can be null
     * @return properties of the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Modifying.html#change-properties-of-a-collection">API
     *      Documentation</a>
     */
    CollectionPropertiesEntity changeProperties(CollectionPropertiesOptions options) throws C8DBException;

    /**
     * Renames the collection
     *
     * @param newName The new name
     * @return information about the collection
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/Collection/Modifying.html#rename-collection">API
     *      Documentation</a>
     */
    CollectionEntity rename(String newName) throws C8DBException;

    /**
     * Grants or revoke access to the collection for user user. You need permission
     * to the _system database in order to execute this call.
     *
     * @param user        The name of the user
     * @param permissions The permissions the user grant
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access">
     *      API Documentation</a>
     */
    void grantAccess(String user, Permissions permissions) throws C8DBException;

    /**
     * Revokes access to the collection for user user. You need permission to the
     * _system database in order to execute this call.
     *
     * @param user The name of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access">
     *      API Documentation</a>
     */
    void revokeAccess(String user) throws C8DBException;

    /**
     * Clear the collection access level, revert back to the default access level.
     *
     * @param user The name of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access">
     *      API Documentation</a>
     * @since ArangoDB 3.2.0
     */
    void resetAccess(String user) throws C8DBException;

    /**
     * Get the collection access level
     *
     * @param user The name of the user
     * @return permissions of the user
     * @throws C8DBException
     * @see <a href=
     *      "https://docs.arangodb.com/current/HTTP/UserManagement/#get-the-specific-collection-access-level">
     *      API Documentation</a>
     * @since ArangoDB 3.2.0
     */
    Permissions getPermissions(String user) throws C8DBException;

}
