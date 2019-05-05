# Manipulating the vertex collection

## C8Graph.vertexCollection

`C8Graph.vertexCollection(String name) : C8VertexCollection`

Returns a _C8VertexCollection_ instance for the given vertex collection name.

**Arguments**

- **name**: `String`

  Name of the vertex collection

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

C8VertexCollection collection = graph.vertexCollection("some-vertex-collection");
```

## C8Graph.getVertexCollections

`C8Graph.getVertexCollections() : Collection<String>`

Fetches all vertex collections from the graph and returns a list of collection names.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

Collection<String> collections = graph.getVertexCollections();
```

## C8Graph.addVertexCollection

`C8Graph.addVertexCollection(String name) : GraphEntity`

Adds a vertex collection to the set of collections of the graph.
If the collection does not exist, it will be created.

**Arguments**

- **name**: `String`

  Name of the vertex collection

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

graph.addVertexCollection("some-other-collection");
```
