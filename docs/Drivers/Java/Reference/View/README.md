# View API

These functions implement the
[HTTP API for views](https://docs.c8db.com/latest/HTTP/Views/index.html).

## Getting information about the view

See
[the HTTP API documentation](https://docs.c8db.com/latest/HTTP/Views/Getting.html)
for details.

## C8View.exists

`C8View.exists() : boolean`

Checks whether the view exists

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8View view = db.view("potatoes");

boolean exists = view.exists();
```

## C8View.getInfo

`C8View.getInfo() : ViewEntity`

Returns information about the view.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8View view = db.view("potatoes");

ViewEntity info = view.getInfo();
```
