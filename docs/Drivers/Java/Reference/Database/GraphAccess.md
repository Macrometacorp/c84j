# Accessing graphs

These functions implement the
[HTTP API for accessing general graphs](https://docs.c8db.com/latest/HTTP/Gharial/index.html).

## C8Database.graph

`C8Database.graph(String name) : C8Graph`

Returns a _C8Graph_ instance for the given graph name.

**Arguments**

- **name**: `String`

  Name of the graph

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("myGraph");
```
