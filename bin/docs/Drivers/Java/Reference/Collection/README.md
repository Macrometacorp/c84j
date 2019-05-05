# Collection API

These functions implement the
[HTTP API for collections](https://docs.c8db.com/latest/HTTP/Collection/index.html).

The _C8Collection_ API is used for all collections, regardless of
their specific type (document/edge collection).

## Getting information about the collection

See
[the HTTP API documentation](https://docs.c8db.com/latest/HTTP/Collection/Getting.html)
for details.

## C8Collection.exists

`C8Collection.exists() : boolean`

Checks whether the collection exists

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("potatoes");

boolean exists = collection.exists();
```

## C8Collection.getInfo

`C8Collection.getInfo() : CollectionEntity`

Returns information about the collection.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("potatoes");

CollectionEntity info = collection.getInfo();
```

## C8Collection.getProperties

`C8Collection.getProperties() : CollectionPropertiesEntity`

Reads the properties of the specified collection.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("potatoes");

CollectionPropertiesEntity properties = collection.getProperties();
```

## C8Collection.getRevision

`C8Collection.getRevision() : CollectionRevisionEntity`

Retrieve the collections revision.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("potatoes");

CollectionRevisionEntity revision = collection.getRevision();
```

## C8Collection.getIndexes

`C8Collection.getIndexes() : Collection<IndexEntity>`

Fetches a list of all indexes on this collection.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("potatoes");

Collection<IndexEntity> indexes = collection.getIndexes();
```
