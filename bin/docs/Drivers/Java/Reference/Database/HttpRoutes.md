# Arbitrary HTTP routes

## C8Database.route

`C8Database.route(String... path) : C8Route`

Returns a new _C8Route_ instance for the given path
(relative to the database) that can be used to perform arbitrary requests.

**Arguments**

- **path**: `String...`

  The database-relative URL of the route

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
C8Route myFoxxService = db.route("my-foxx-service");

VPackSlice body = c8.util().serialize("{'username': 'admin', 'password': 'hunter2'");
Response response = myFoxxService.route("users").withBody(body).post();
// response.getBody() is the result of
// POST /_db/myDB/my-foxx-service/users
// with VelocyPack request body '{"username": "admin", "password": "hunter2"}'
```
