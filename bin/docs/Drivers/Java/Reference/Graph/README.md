# Graph API

These functions implement the
[HTTP API for manipulating graphs](https://docs.c8db.com/latest/HTTP/Gharial/index.html).

## C8Database.createGraph

`C8Database.createGraph(String name, Collection<EdgeDefinition> edgeDefinitions, GraphCreateOptions options) : GraphEntity`

Create a new graph in the graph module. The creation of a graph requires the
name of the graph and a definition of its edges.

**Arguments**

- **name**: `String`

  Name of the graph

- **edgeDefinitions**: `Collection<EdgeDefinition>`

  An array of definitions for the edge

- **options**: `GraphCreateOptions`

  - **orphanCollections**: `String...`

    Additional vertex collections

  - **isSmart**: `Boolean`

    Define if the created graph should be smart.
    This only has effect in Enterprise Edition.

  - **replicationFactor**: `Integer`

    (The default is 1): in a cluster, this attribute determines how many copies
    of each shard are kept on different DBServers. The value 1 means that only
    one copy (no synchronous replication) is kept. A value of k means that k-1
    replicas are kept. Any two copies reside on different DBServers.
    Replication between them is synchronous, that is, every write operation to
    the "leader" copy will be replicated to all "follower" replicas, before the
    write operation is reported successful. If a server fails, this is detected
    automatically and one of the servers holding copies take over, usually
    without an error being reported.

  - **numberOfShards**: `Integer`

    The number of shards that is used for every collection within this graph.
    Cannot be modified later.

  - **smartGraphAttribute**: `String`

    The attribute name that is used to smartly shard the vertices of a graph.
    Every vertex in this Graph has to have this attribute. Cannot be modified later.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

EdgeDefinition edgeDefinition = new EdgeDefinition()
                                  .collection("edges")
                                  .from("start-vertices")
                                  .to("end-vertices");
GraphEntity graph = db.createGraph(
  "some-graph", Arrays.asList(edgeDefinition), new GraphCreateOptions()
);
// graph now exists
```

## C8Graph.create

`C8Graph.create(Collection<EdgeDefinition> edgeDefinitions, GraphCreateOptions options) : GraphEntity`

Create a new graph in the graph module. The creation of a graph requires the
name of the graph and a definition of its edges.

Alternative for [C8Database.createGraph](#c8databasecreategraph).

**Arguments**

- **edgeDefinitions**: `Collection<EdgeDefinition>`

  An array of definitions for the edge

- **options**: `GraphCreateOptions`

  - **orphanCollections**: `String...`

    Additional vertex collections

  - **isSmart**: `Boolean`

    Define if the created graph should be smart.
    This only has effect in Enterprise Edition.

  - **replicationFactor**: `Integer`

    (The default is 1): in a cluster, this attribute determines how many copies
    of each shard are kept on different DBServers. The value 1 means that only
    one copy (no synchronous replication) is kept. A value of k means that k-1
    replicas are kept. Any two copies reside on different DBServers.
    Replication between them is synchronous, that is, every write operation to
    the "leader" copy will be replicated to all "follower" replicas, before the
    write operation is reported successful. If a server fails, this is detected
    automatically and one of the servers holding copies take over, usually
    without an error being reported.

  - **numberOfShards**: `Integer`

    The number of shards that is used for every collection within this graph.
    Cannot be modified later.

  - **smartGraphAttribute**: `String`

    The attribute name that is used to smartly shard the vertices of a graph.
    Every vertex in this Graph has to have this attribute. Cannot be modified later.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Graph graph = db.graph("some-graph");
EdgeDefinition edgeDefinition = new EdgeDefinition()
                                  .collection("edges")
                                  .from("start-vertices")
                                  .to("end-vertices");
graph.create(Arrays.asList(edgeDefinition), new GraphCreateOptions());
// graph now exists
```

## C8Graph.exists

`C8Graph.exists() : boolean`

Checks whether the graph exists

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Graph graph = db.graph("some-graph");
boolean exists = graph.exists();
```

## C8Graph.getInfo

`C8Graph.getInfo() : GraphEntity`

Retrieves general information about the graph.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Graph graph = db.graph("some-graph");
GraphEntity info = graph.getInfo();
```

## C8Graph.drop

`C8Graph.drop(boolean dropCollections) : void`

Deletes the graph from the database.

**Arguments**

- **dropCollections**: `boolean`

  Drop collections of this graph as well. Collections will only be dropped if
  they are not used in other graphs.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Graph graph = db.graph("some-graph");
graph.drop();
// the graph "some-graph" no longer exists
```
