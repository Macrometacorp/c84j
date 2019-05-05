# Accessing views

These functions implement the
[HTTP API for accessing view](https://docs.c8db.com/latest/HTTP/Views/Getting.html).

## C8Database.view

`C8Database.view(String name) : C8View`

Returns a _C8View_ instance for the given view name.

**Arguments**

- **name**: `String`

  Name of the view

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8View view = db.view("myView");
```

## C8Database.c8Search

`C8Database.c8Search(String name) : C8Search`

Returns a _C8Search_ instance for the given C8Search view name.

**Arguments**

- **name**: `String`

  Name of the view

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Search view = db.c8Search("myC8SearchView");
```

## C8Database.getViews

`C8Database.getViews() : Collection<ViewEntity>`

Fetches all views from the database and returns an list of collection descriptions.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
Collection<ViewEntity> infos = db.getViews();
```
