# Manipulating the view

These functions implement
[the HTTP API for modifying views](https://docs.c8db.com/latest/HTTP/Views/Modifying.html).

## C8Database.createView

`C8Database.createView(String name, ViewType type) : ViewEntity`

Creates a view of the given _type_, then returns view information from the server.

**Arguments**

- **name**: `String`

  The name of the view

- **type**: `ViewType`

  The type of the view

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.createView("myView", ViewType.c8_SEARCH);
// the view "potatoes" now exists
```

## C8View.rename

`C8View.rename(String newName) : ViewEntity`

Renames the view.

**Arguments**

- **newName**: `String`

  The new name

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8View view = db.view("some-view");

ViewEntity result = view.rename("new-view-name")
assertThat(result.getName(), is("new-view-name");
// result contains additional information about the view
```

## C8View.drop

`C8View.drop() : void`

Deletes the view from the database.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8View view = db.view("some-view");

view.drop();
// the view "some-view" no longer exists
```
