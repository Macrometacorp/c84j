# Accessing collections

These functions implement the
[HTTP API for accessing collections](https://docs.c8db.com/latest/HTTP/Collection/Getting.html).

## C8Database.collection

`C8Database.collection(String name) : C8Collection`

Returns a _C8Collection_ instance for the given collection name.

**Arguments**

- **name**: `String`

  Name of the collection

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Collection collection = db.collection("myCollection");
```

## C8Database.getCollections

`C8Database.getCollections() : Collection<CollectionEntity>`

Fetches all collections from the database and returns an list of collection descriptions.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
Collection<CollectionEntity> infos = db.getCollections();
```
