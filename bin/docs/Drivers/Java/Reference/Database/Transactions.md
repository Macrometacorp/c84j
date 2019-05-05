# Transactions

This function implements the
[HTTP API for transactions](https://docs.c8db.com/latest/HTTP/Transaction/index.html).

## C8Database.transaction

`C8Database.transaction(String action, Class<T> type, TransactionOptions options) : T`

Performs a server-side transaction and returns its return value.

**Arguments**

- **action**: `String`

  A String evaluating to a JavaScript function to be executed on the server.

- **type**: `Class`

  The type of the result (POJO class, `VPackSlice` or `String` for JSON)

- **options**: `TransactionOptions`

  Additional transaction options

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
String action = "function (params) {"
                + "const db = require('@c8db').db;"
                + "return db._query('FOR i IN test RETURN i._key').toArray();"
              + "}";
String[] keys = c8.db().transaction(
  action, String[].class, new TransactionOptions()
);
```
