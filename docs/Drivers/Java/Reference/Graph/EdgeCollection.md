# Manipulating the edge collection

## C8Graph.edgeCollection

`C8Graph.edgeCollection(String name) : C8EdgeCollection`

Returns a _C8EdgeCollection_ instance for the given edge collection name.

**Arguments**

- **name**: `String`

  Name of the edge collection

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

C8EdgeCollection collection = graph.edgeCollection("some-edge-collection");
```

## C8Graph.getEdgeDefinitions

`C8Graph.getEdgeDefinitions() : Collection<String>`

Fetches all edge collections from the graph and returns a list of collection names.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

Collection<String> collections = graph.getEdgeDefinitions();
```

## C8Graph.addEdgeDefinition

`C8Graph.addEdgeDefinition(EdgeDefinition definition) : GraphEntity`

Adds the given edge definition to the graph.

**Arguments**

- **definition**: `EdgeDefinition`

  The edge definition

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

EdgeDefinition edgeDefinition = new EdgeDefinition()
                                  .collection("edges")
                                  .from("start-vertices")
                                  .to("end-vertices");
graph.addEdgeDefinition(edgeDefinition);
// the edge definition has been added to the graph
```

## C8Graph.replaceEdgeDefinition

`C8Graph.replaceEdgeDefinition(EdgeDefinition definition) : GraphEntity`

Change one specific edge definition. This will modify all occurrences of this
definition in all graphs known to your database.

**Arguments**

- **definition**: `EdgeDefinition`

  The edge definition

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

EdgeDefinition edgeDefinition = new EdgeDefinition()
                                  .collection("edges")
                                  .from("start-vertices")
                                  .to("end-vertices");
graph.replaceEdgeDefinition(edgeDefinition);
// the edge definition has been modified
```

## C8Graph.removeEdgeDefinition

`C8Graph.removeEdgeDefinition(String definitionName) : GraphEntity`

Remove one edge definition from the graph. This will only remove the
edge collection, the vertex collections remain untouched and can still
be used in your queries.

**Arguments**

- **definitionName**: `String`

  The name of the edge collection used in the definition

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Graph graph = db.graph("some-graph");

EdgeDefinition edgeDefinition = new EdgeDefinition()
                                  .collection("edges")
                                  .from("start-vertices")
                                  .to("end-vertices");
graph.addEdgeDefinition(edgeDefinition);
// the edge definition has been added to the graph

graph.removeEdgeDefinition("edges");
// the edge definition has been removed
```
