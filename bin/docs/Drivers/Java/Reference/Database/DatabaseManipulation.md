# Manipulation databases

These functions implement the
[HTTP API for manipulating databases](https://docs.c8db.com/latest/HTTP/Database/index.html).

## C8DB.createDatabase

`C8DB.createDatabase(String name) : Boolean`

Creates a new database with the given name.

**Arguments**

- **name**: `String`

  Name of the database to create

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
c8.createDatabase("myDB");
```

## C8Database.create()

`C8Database.create() : Boolean`

Creates the database.

Alternative for [C8DB.createDatabase](#c8dbcreatedatabase).

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.create();
```

## C8Database.exists()

`C8Database.exists() : boolean`

Checks whether the database exists

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
boolean exists = db.exists();
```

## C8Database.getInfo

`C8Database.getInfo() : DatabaseEntity`

Retrieves information about the current database

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
DatabaseEntity info = db.getInfo();
```

## C8DB.getDatabases

`C8DB.getDatabases() : Collection<String>`

Retrieves a list of all existing databases

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
Collection<String> names = c8.getDatabases();
```

## C8Database.drop

`C8Database.drop() : Boolean`

Deletes the database from the server.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.drop();
```
