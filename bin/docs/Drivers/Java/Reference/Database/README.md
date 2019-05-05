# Database API

## C8DB.db

`C8DB.db(String name) : C8Database`

Returns a _C8Database_ instance for the given database name.

**Arguments**

- **name**: `String`

  Name of the database

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
```
