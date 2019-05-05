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
 */

package com.c8;

import java.util.Collection;

import com.c8.entity.CollectionEntity;
import com.c8.entity.CollectionPropertiesEntity;
import com.c8.entity.CollectionRevisionEntity;
import com.c8.entity.DocumentCreateEntity;
import com.c8.entity.DocumentDeleteEntity;
import com.c8.entity.DocumentImportEntity;
import com.c8.entity.DocumentUpdateEntity;
import com.c8.entity.IndexEntity;
import com.c8.entity.MultiDocumentEntity;
import com.c8.entity.Permissions;
import com.c8.model.CollectionCreateOptions;
import com.c8.model.CollectionPropertiesOptions;
import com.c8.model.DocumentCreateOptions;
import com.c8.model.DocumentDeleteOptions;
import com.c8.model.DocumentExistsOptions;
import com.c8.model.DocumentImportOptions;
import com.c8.model.DocumentReadOptions;
import com.c8.model.DocumentReplaceOptions;
import com.c8.model.DocumentUpdateOptions;
import com.c8.model.FulltextIndexOptions;
import com.c8.model.GeoIndexOptions;
import com.c8.model.HashIndexOptions;
import com.c8.model.PersistentIndexOptions;
import com.c8.model.SkiplistIndexOptions;

/**
 * Interface for operations on C8DB collection level.
 * 
 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/">Collection API Documentation</a>
 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/">Documents API Documentation</a>
 * @author Mark Vollmary
 */
public interface C8Collection extends C8SerializationAccessor {

	/**
	 * The the handler of the database the collection is within
	 * 
	 * @return database handler
	 */
	public C8Database db();

	/**
	 * The name of the collection
	 * 
	 * @return collection name
	 */
	public String name();

	/**
	 * Creates a new document from the given document, unless there is already a document with the _key given. If no
	 * _key is given, a new unique _key is generated automatically.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
	 *      Documentation</a>
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentCreateEntity<T> insertDocument(T value) throws C8DBException;

	/**
	 * Creates a new document from the given document, unless there is already a document with the _key given. If no
	 * _key is given, a new unique _key is generated automatically.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
	 *      Documentation</a>
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentCreateEntity<T> insertDocument(T value, DocumentCreateOptions options) throws C8DBException;

	/**
	 * Creates new documents from the given documents, unless there is already a document with the _key given. If no
	 * _key is given, a new unique _key is generated automatically.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
	 *      Documentation</a>
	 * @param values
	 *            A List of documents (POJO, VPackSlice or String for JSON)
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(Collection<T> values) throws C8DBException;

	/**
	 * Creates new documents from the given documents, unless there is already a document with the _key given. If no
	 * _key is given, a new unique _key is generated automatically.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#create-document">API
	 *      Documentation</a>
	 * @param values
	 *            A List of documents (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentCreateEntity<T>> insertDocuments(
		Collection<T> values,
		DocumentCreateOptions options) throws C8DBException;

	/**
	 * Bulk imports the given values into the collection.
	 * 
	 * @param values
	 *            a list of Objects that will be stored as documents
	 * @return information about the import
	 * @throws C8DBException
	 */
	DocumentImportEntity importDocuments(Collection<?> values) throws C8DBException;

	/**
	 * Bulk imports the given values into the collection.
	 * 
	 * @param values
	 *            a list of Objects that will be stored as documents
	 * @param options
	 *            Additional options, can be null
	 * @return information about the import
	 * @throws C8DBException
	 */
	DocumentImportEntity importDocuments(Collection<?> values, DocumentImportOptions options) throws C8DBException;

	/**
	 * Bulk imports the given values into the collection.
	 * 
	 * @param values
	 *            JSON-encoded array of objects that will be stored as documents
	 * @return information about the import
	 * @throws C8DBException
	 */
	DocumentImportEntity importDocuments(String values) throws C8DBException;

	/**
	 * Bulk imports the given values into the collection.
	 * 
	 * @param values
	 *            JSON-encoded array of objects that will be stored as documents
	 * @param options
	 *            Additional options, can be null
	 * @return information about the import
	 * @throws C8DBException
	 */
	DocumentImportEntity importDocuments(String values, DocumentImportOptions options) throws C8DBException;

	/**
	 * Retrieves the document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON)
	 * @return the document identified by the key
	 * @throws C8DBException
	 */
	<T> T getDocument(String key, Class<T> type) throws C8DBException;

	/**
	 * Retrieves the document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return the document identified by the key
	 * @throws C8DBException
	 */
	<T> T getDocument(String key, Class<T> type, DocumentReadOptions options) throws C8DBException;

	/**
	 * Retrieves multiple documents with the given {@code _key} from the collection.
	 * 
	 * @param keys
	 *            The keys of the documents
	 * @param type
	 *            The type of the documents (POJO class, VPackSlice or String for JSON)
	 * @return the documents and possible errors
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type) throws C8DBException;

	/**
	 * Retrieves multiple documents with the given {@code _key} from the collection.
	 * 
	 * @param keys
	 *            The keys of the documents
	 * @param type
	 *            The type of the documents (POJO class, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return the documents and possible errors
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<T> getDocuments(Collection<String> keys, Class<T> type, DocumentReadOptions options)
			throws C8DBException;

	/**
	 * Replaces the document with {@code key} with the one in the body, provided there is such a document and no
	 * precondition is violated
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#replace-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentUpdateEntity<T> replaceDocument(String key, T value) throws C8DBException;

	/**
	 * Replaces the document with {@code key} with the one in the body, provided there is such a document and no
	 * precondition is violated
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#replace-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentUpdateEntity<T> replaceDocument(String key, T value, DocumentReplaceOptions options)
			throws C8DBException;

	/**
	 * Replaces multiple documents in the specified collection with the ones in the values, the replaced documents are
	 * specified by the _key attributes in the documents in values.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#replace-documents">API
	 *      Documentation</a>
	 * @param values
	 *            A List of documents (POJO, VPackSlice or String for JSON)
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(Collection<T> values) throws C8DBException;

	/**
	 * Replaces multiple documents in the specified collection with the ones in the values, the replaced documents are
	 * specified by the _key attributes in the documents in values.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#replace-documents">API
	 *      Documentation</a>
	 * @param values
	 *            A List of documents (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentUpdateEntity<T>> replaceDocuments(
		Collection<T> values,
		DocumentReplaceOptions options) throws C8DBException;

	/**
	 * Partially updates the document identified by document-key. The value must contain a document with the attributes
	 * to patch (the patch document). All attributes from the patch document will be added to the existing document if
	 * they do not yet exist, and overwritten in the existing document if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#update-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentUpdateEntity<T> updateDocument(String key, T value) throws C8DBException;

	/**
	 * Partially updates the document identified by document-key. The value must contain a document with the attributes
	 * to patch (the patch document). All attributes from the patch document will be added to the existing document if
	 * they do not yet exist, and overwritten in the existing document if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#update-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param value
	 *            A representation of a single document (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentUpdateEntity<T> updateDocument(String key, T value, DocumentUpdateOptions options)
			throws C8DBException;

	/**
	 * Partially updates documents, the documents to update are specified by the _key attributes in the objects on
	 * values. Vales must contain a list of document updates with the attributes to patch (the patch documents). All
	 * attributes from the patch documents will be added to the existing documents if they do not yet exist, and
	 * overwritten in the existing documents if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#update-documents">API
	 *      Documentation</a>
	 * @param values
	 *            A list of documents (POJO, VPackSlice or String for JSON)
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(Collection<T> values) throws C8DBException;

	/**
	 * Partially updates documents, the documents to update are specified by the _key attributes in the objects on
	 * values. Vales must contain a list of document updates with the attributes to patch (the patch documents). All
	 * attributes from the patch documents will be added to the existing documents if they do not yet exist, and
	 * overwritten in the existing documents if they do exist there.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#update-documents">API
	 *      Documentation</a>
	 * @param values
	 *            A list of documents (POJO, VPackSlice or String for JSON)
	 * @param options
	 *            Additional options, can be null
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentUpdateEntity<T>> updateDocuments(
		Collection<T> values,
		DocumentUpdateOptions options) throws C8DBException;

	/**
	 * Deletes the document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#removes-a-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON). Only necessary if
	 *            options.returnOld is set to true, otherwise can be null.
	 * @param options
	 *            Additional options, can be null
	 * @return information about the document
	 * @throws C8DBException
	 */
	DocumentDeleteEntity<Void> deleteDocument(String key) throws C8DBException;

	/**
	 * Deletes the document with the given {@code key} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#removes-a-document">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param type
	 *            The type of the document (POJO class, VPackSlice or String for JSON). Only necessary if
	 *            options.returnOld is set to true, otherwise can be null.
	 * @param options
	 *            Additional options, can be null
	 * @return information about the document
	 * @throws C8DBException
	 */
	<T> DocumentDeleteEntity<T> deleteDocument(String key, Class<T> type, DocumentDeleteOptions options)
			throws C8DBException;

	/**
	 * Deletes multiple documents from the collection.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#removes-multiple-documents">API
	 *      Documentation</a>
	 * @param values
	 *            The keys of the documents or the documents themselves
	 * @param type
	 *            The type of the documents (POJO class, VPackSlice or String for JSON). Only necessary if
	 *            options.returnOld is set to true, otherwise can be null.
	 * @return information about the documents
	 * @throws C8DBException
	 */
	MultiDocumentEntity<DocumentDeleteEntity<Void>> deleteDocuments(Collection<?> values) throws C8DBException;

	/**
	 * Deletes multiple documents from the collection.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#removes-multiple-documents">API
	 *      Documentation</a>
	 * @param values
	 *            The keys of the documents or the documents themselves
	 * @param type
	 *            The type of the documents (POJO class, VPackSlice or String for JSON). Only necessary if
	 *            options.returnOld is set to true, otherwise can be null.
	 * @param options
	 *            Additional options, can be null
	 * @return information about the documents
	 * @throws C8DBException
	 */
	<T> MultiDocumentEntity<DocumentDeleteEntity<T>> deleteDocuments(
		Collection<?> values,
		Class<T> type,
		DocumentDeleteOptions options) throws C8DBException;

	/**
	 * Checks if the document exists by reading a single document head
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document-header">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @return true if the document was found, otherwise false
	 */
	Boolean documentExists(String key);

	/**
	 * Checks if the document exists by reading a single document head
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Document/WorkingWithDocuments.html#read-document-header">API
	 *      Documentation</a>
	 * @param key
	 *            The key of the document
	 * @param options
	 *            Additional options, can be null
	 * @return true if the document was found, otherwise false
	 * @throws C8DBException
	 *             only thrown when {@link DocumentExistsOptions#isCatchException()} == false
	 */
	Boolean documentExists(String key, DocumentExistsOptions options) throws C8DBException;

	/**
	 * Fetches information about the index with the given {@code id} and returns it.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/WorkingWith.html#read-index">API Documentation</a>
	 * @param id
	 *            The index-handle
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity getIndex(String id) throws C8DBException;

	/**
	 * Deletes the index with the given {@code id} from the collection.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/WorkingWith.html#delete-index">API Documentation</a>
	 * @param id
	 *            The index-handle
	 * @return the id of the index
	 * @throws C8DBException
	 */
	String deleteIndex(String id) throws C8DBException;

	/**
	 * Creates a hash index for the collection if it does not already exist.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/Hash.html#create-hash-index">API Documentation</a>
	 * @param fields
	 *            A list of attribute paths
	 * @param options
	 *            Additional options, can be null
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity ensureHashIndex(Iterable<String> fields, HashIndexOptions options) throws C8DBException;

	/**
	 * Creates a skip-list index for the collection, if it does not already exist.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/Skiplist.html#create-skip-list">API
	 *      Documentation</a>
	 * @param fields
	 *            A list of attribute paths
	 * @param options
	 *            Additional options, can be null
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity ensureSkiplistIndex(Iterable<String> fields, SkiplistIndexOptions options) throws C8DBException;

	/**
	 * Creates a persistent index for the collection, if it does not already exist.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/Persistent.html#create-a-persistent-index">API
	 *      Documentation</a>
	 * @param fields
	 *            A list of attribute paths
	 * @param options
	 *            Additional options, can be null
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity ensurePersistentIndex(Iterable<String> fields, PersistentIndexOptions options) throws C8DBException;

	/**
	 * Creates a geo-spatial index for the collection, if it does not already exist.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/Geo.html#create-geospatial-index">API
	 *      Documentation</a>
	 * @param fields
	 *            A list of attribute paths
	 * @param options
	 *            Additional options, can be null
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity ensureGeoIndex(Iterable<String> fields, GeoIndexOptions options) throws C8DBException;

	/**
	 * Creates a fulltext index for the collection, if it does not already exist.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Indexes/Fulltext.html#create-fulltext-index">API
	 *      Documentation</a>
	 * @param fields
	 *            A list of attribute paths
	 * @param options
	 *            Additional options, can be null
	 * @return information about the index
	 * @throws C8DBException
	 */
	IndexEntity ensureFulltextIndex(Iterable<String> fields, FulltextIndexOptions options) throws C8DBException;

	/**
	 * Fetches a list of all indexes on this collection.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Indexes/WorkingWith.html#read-all-indexes-of-a-collection">API
	 *      Documentation</a>
	 * @return information about the indexes
	 * @throws C8DBException
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
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#truncate-collection">API
	 *      Documentation</a>
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity truncate() throws C8DBException;

	/**
	 * Counts the documents in a collection
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Collection/Getting.html#return-number-of-documents-in-a-collection">API
	 *      Documentation</a>
	 * @return information about the collection, including the number of documents
	 * @throws C8DBException
	 */
	CollectionPropertiesEntity count() throws C8DBException;

	/**
	 * Creates a collection for this collection's name, then returns collection information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#create-collection">API
	 *      Documentation</a>
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity create() throws C8DBException;

	/**
	 * Creates a collection with the given {@code options} for this collection's name, then returns collection
	 * information from the server.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#create-collection">API
	 *      Documentation</a>
	 * @param options
	 *            Additional options, can be null
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity create(CollectionCreateOptions options) throws C8DBException;

	/**
	 * Deletes the collection from the database.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#drops-collection">API
	 *      Documentation</a>
	 * @throws C8DBException
	 */
	void drop() throws C8DBException;

	/**
	 * Deletes the collection from the database.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Creating.html#drops-collection">API
	 *      Documentation</a>
	 * @param isSystem
	 *            Whether or not the collection to drop is a system collection. This parameter must be set to true in
	 *            order to drop a system collection.
	 * @since C8DB 3.1.0
	 * @throws C8DBException
	 */
	void drop(boolean isSystem) throws C8DBException;

	/**
	 * Tells the server to load the collection into memory.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Modifying.html#load-collection">API
	 *      Documentation</a>
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity load() throws C8DBException;

	/**
	 * Tells the server to remove the collection from memory. This call does not delete any documents. You can use the
	 * collection afterwards; in which case it will be loaded into memory, again.
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Modifying.html#unload-collection">API
	 *      Documentation</a>
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity unload() throws C8DBException;

	/**
	 * Returns information about the collection
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Collection/Getting.html#return-information-about-a-collection">API
	 *      Documentation</a>
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity getInfo() throws C8DBException;

	/**
	 * Reads the properties of the specified collection
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Collection/Getting.html#read-properties-of-a-collection">API
	 *      Documentation</a>
	 * @return properties of the collection
	 * @throws C8DBException
	 */
	CollectionPropertiesEntity getProperties() throws C8DBException;

	/**
	 * Changes the properties of the collection
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/Collection/Modifying.html#change-properties-of-a-collection">API
	 *      Documentation</a>
	 * @param options
	 *            Additional options, can be null
	 * @return properties of the collection
	 * @throws C8DBException
	 */
	CollectionPropertiesEntity changeProperties(CollectionPropertiesOptions options) throws C8DBException;

	/**
	 * Renames the collection
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Modifying.html#rename-collection">API
	 *      Documentation</a>
	 * @param newName
	 *            The new name
	 * @return information about the collection
	 * @throws C8DBException
	 */
	CollectionEntity rename(String newName) throws C8DBException;

	/**
	 * Retrieve the collections revision
	 * 
	 * @see <a href="https://docs.c8db.com/current/HTTP/Collection/Getting.html#return-collection-revision-id">API
	 *      Documentation</a>
	 * @return information about the collection, including the collections revision
	 * @throws C8DBException
	 */
	CollectionRevisionEntity getRevision() throws C8DBException;

	/**
	 * Grants or revoke access to the collection for user user. You need permission to the _system database in order to
	 * execute this call.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access"> API
	 *      Documentation</a>
	 * @param user
	 *            The name of the user
	 * @param permissions
	 *            The permissions the user grant
	 * @throws C8DBException
	 */
	void grantAccess(String user, Permissions permissions) throws C8DBException;

	/**
	 * Revokes access to the collection for user user. You need permission to the _system database in order to execute
	 * this call.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access"> API
	 *      Documentation</a>
	 * @param user
	 *            The name of the user
	 * @throws C8DBException
	 */
	void revokeAccess(String user) throws C8DBException;

	/**
	 * Clear the collection access level, revert back to the default access level.
	 * 
	 * @see <a href=
	 *      "https://docs.c8db.com/current/HTTP/UserManagement/index.html#grant-or-revoke-collection-access"> API
	 *      Documentation</a>
	 * @param user
	 *            The name of the user
	 * @since C8DB 3.2.0
	 * @throws C8DBException
	 */
	void resetAccess(String user) throws C8DBException;

	/**
	 * Get the collection access level
	 * 
	 * @see <a href= "https://docs.c8db.com/current/HTTP/UserManagement/#get-the-specific-collection-access-level">
	 *      API Documentation</a>
	 * @param user
	 *            The name of the user
	 * @return permissions of the user
	 * @since C8DB 3.2.0
	 * @throws C8DBException
	 */
	Permissions getPermissions(String user) throws C8DBException;

}
