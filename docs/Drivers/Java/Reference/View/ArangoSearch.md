# C8Search API

These functions implement the
[HTTP API for C8Search views](https://docs.c8db.com/latest/HTTP/Views/C8Search.html).

## C8Database.createC8Search

`C8Database.createC8Search(String name, C8SearchCreateOptions options) : ViewEntity`

Creates a C8Search view with the given _options_, then returns
view information from the server.

**Arguments**

- **name**: `String`

  The name of the view

- **options**: `C8SearchCreateOptions`

  - **consolidationIntervalMsec**: `Long`

    Wait at least this many milliseconds between committing index data changes
    and making them visible to queries (default: 60000, to disable use: 0).
    For the case where there are a lot of inserts/updates, a lower value,
    until commit, will cause the index not to account for them and memory usage
    would continue to grow. For the case where there are a few inserts/updates,
    a higher value will impact performance and waste disk space for each
    commit call without any added benefits.

  - **cleanupIntervalStep**: `Long`

    Wait at least this many commits between removing unused files in
    data directory (default: 10, to disable use: 0). For the case where the
    consolidation policies merge segments often (i.e. a lot of commit+consolidate),
    a lower value will cause a lot of disk space to be wasted. For the case
    where the consolidation policies rarely merge segments (i.e. few inserts/deletes),
    a higher value will impact performance without any added benefits.

  - **consolidationPolicy**:

    - **type**: `ConsolidationType`

      The type of the consolidation policy.

    - **threshold**: `Double`

      Select a given segment for "consolidation" if and only if the formula
      based on type (as defined above) evaluates to true, valid value range
      [0.0, 1.0] (default: 0.85)

    - **segmentThreshold**: `Long`

      Apply the "consolidation" operation if and only if (default: 300):
      `{segmentThreshold} < number_of_segments`

    - **link**: `CollectionLink[]`

      A list of linked collections

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.createC8Search("potatoes", new C8SearchPropertiesOptions());
// the C8Search view "potatoes" now exists
```

## C8Search.create

`C8Search.create(C8SearchCreateOptions options) : ViewEntity`

Creates a C8Search view with the given _options_, then returns view information from the server.

Alternative for `C8Database.createC8Search`.

**Arguments**

- **options**: `C8SearchCreateOptions`

  - **consolidationIntervalMsec**: `Long`

    Wait at least this many milliseconds between committing index data changes
    and making them visible to queries (default: 60000, to disable use: 0).
    For the case where there are a lot of inserts/updates, a lower value,
    until commit, will cause the index not to account for them and memory usage
    would continue to grow. For the case where there are a few inserts/updates,
    a higher value will impact performance and waste disk space for each
    commit call without any added benefits.

  - **cleanupIntervalStep**: `Long`

    Wait at least this many commits between removing unused files in
    data directory (default: 10, to disable use: 0). For the case where the
    consolidation policies merge segments often (i.e. a lot of commit+consolidate),
    a lower value will cause a lot of disk space to be wasted. For the case
    where the consolidation policies rarely merge segments (i.e. few inserts/deletes),
    a higher value will impact performance without any added benefits.

  - **consolidationPolicy**:

    - **type**: `ConsolidationType`

      The type of the consolidation policy.

    - **threshold**: `Double`

      Select a given segment for "consolidation" if and only if the formula
      based on type (as defined above) evaluates to true, valid value range
      [0.0, 1.0] (default: 0.85)

    - **segmentThreshold**: `Long`

      Apply the "consolidation" operation if and only if (default: 300):
      `{segmentThreshold} < number_of_segments`

    - **link**: `CollectionLink[]`

      A list of linked collections

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Search view = db.c8Search("potatoes");

view.create(new C8SearchPropertiesOptions());
// the C8Search view "potatoes" now exists
```

## C8Search.getProperties

`C8Search.getProperties() : C8SearchPropertiesEntity`

Reads the properties of the specified view.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Search view = db.c8Search("potatoes");

C8SearchPropertiesEntity properties = view.getProperties();
```

## C8Search.updateProperties

`C8Search.updateProperties(C8SearchPropertiesOptions options) : C8SearchPropertiesEntity`

Partially changes properties of the view.

**Arguments**

- **options**: `C8SearchPropertiesOptions`

  - **consolidationIntervalMsec**: `Long`

    Wait at least this many milliseconds between committing index data changes
    and making them visible to queries (default: 60000, to disable use: 0).
    For the case where there are a lot of inserts/updates, a lower value,
    until commit, will cause the index not to account for them and memory usage
    would continue to grow. For the case where there are a few inserts/updates,
    a higher value will impact performance and waste disk space for each
    commit call without any added benefits.

  - **cleanupIntervalStep**: `Long`

    Wait at least this many commits between removing unused files in
    data directory (default: 10, to disable use: 0). For the case where the
    consolidation policies merge segments often (i.e. a lot of commit+consolidate),
    a lower value will cause a lot of disk space to be wasted. For the case
    where the consolidation policies rarely merge segments (i.e. few inserts/deletes),
    a higher value will impact performance without any added benefits.

  - **consolidationPolicy**:

    - **type**: `ConsolidationType`

      The type of the consolidation policy.

    - **threshold**: `Double`

      Select a given segment for "consolidation" if and only if the formula
      based on type (as defined above) evaluates to true, valid value range
      [0.0, 1.0] (default: 0.85)

    - **segmentThreshold**: `Long`

      Apply the "consolidation" operation if and only if (default: 300):
      `{segmentThreshold} < number_of_segments`

    - **link**: `CollectionLink[]`

      A list of linked collections

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Search view = db.c8Search("some-view");

view.updateProperties(
  new C8SearchPropertiesOptions()
    .link(CollectionLink.on("myCollection").fields(FieldLink.on("value").analyzers("identity")))
);
```

## C8Search.replaceProperties

`C8Search.replaceProperties(C8SearchPropertiesOptions options) : C8SearchPropertiesEntity`

Changes properties of the view.

**Arguments**

- **options**: `C8SearchPropertiesOptions`

  - **consolidationIntervalMsec**: `Long`

    Wait at least this many milliseconds between committing index data changes
    and making them visible to queries (default: 60000, to disable use: 0).
    For the case where there are a lot of inserts/updates, a lower value,
    until commit, will cause the index not to account for them and memory usage
    would continue to grow. For the case where there are a few inserts/updates,
    a higher value will impact performance and waste disk space for each
    commit call without any added benefits.

  - **cleanupIntervalStep**: `Long`

    Wait at least this many commits between removing unused files in
    data directory (default: 10, to disable use: 0). For the case where the
    consolidation policies merge segments often (i.e. a lot of commit+consolidate),
    a lower value will cause a lot of disk space to be wasted. For the case
    where the consolidation policies rarely merge segments (i.e. few inserts/deletes),
    a higher value will impact performance without any added benefits.

  - **consolidationPolicy**:

    - **type**: `ConsolidationType`

      The type of the consolidation policy.

    - **threshold**: `Double`

      Select a given segment for "consolidation" if and only if the formula
      based on type (as defined above) evaluates to true, valid value range
      [0.0, 1.0] (default: 0.85)

    - **segmentThreshold**: `Long`

      Apply the "consolidation" operation if and only if (default: 300):
      `{segmentThreshold} < number_of_segments`

    - **link**: `CollectionLink[]`

      A list of linked collections

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Search view = db.c8Search("some-view");

view.replaceProperties(
  new C8SearchPropertiesOptions()
    .link(CollectionLink.on("myCollection").fields(FieldLink.on("value").analyzers("identity")))
);
```
