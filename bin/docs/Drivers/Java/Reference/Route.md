# Route API

_C8Route_ instances provide access for arbitrary HTTP requests.
This allows easy access to Foxx services and other HTTP APIs not covered
by the driver itself.

## C8Route.route

`C8Route.route(String... path) : C8Route`

Returns a new _C8Route_ instance for the given path (relative to the
current route) that can be used to perform arbitrary requests.

**Arguments**

- **path**: `String...`

  The relative URL of the route

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route users = route.route("users");
// equivalent to db.route("my-foxx-service/users")
// or db.route("my-foxx-service", "users")
```

## C8Route.withHeader

`C8Route.withHeader(String key, Object value) : C8Route`

Header that should be sent with each request to the route.

**Arguments**

- **key**: `String`

  Header key

- **value**: `Object`

  Header value (the _toString()_ method will be called for the value}

## C8Route.withQueryParam

`C8Route.withQueryParam(String key, Object value) : C8Route`

Query parameter that should be sent with each request to the route.

**Arguments**

- **key**: `String`

  Query parameter key

- **value**: `Object`

  Query parameter value (the _toString()_ method will be called for the value}

## C8Route.withBody

`C8Route.withBody(Object body) : C8Route`

The response body. The body will be serialized to _VPackSlice_.

**Arguments**

- **body**: `Object`

  The request body

## C8Route.delete

`C8Route.delete() : Response`

Performs a DELETE request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route route = route.delete()
// response.getBody() is the response body of calling
// DELETE _db/_system/my-foxx-service

// -- or --

C8Route route = route.route("users/admin").delete()
// response.getBody() is the response body of calling
// DELETE _db/_system/my-foxx-service/users/admin

// -- or --

C8Route route = route.route("users/admin").withQueryParam("permanent", true).delete()
// response.getBody() is the response body of calling
// DELETE _db/_system/my-foxx-service/users/admin?permanent=true
```

## C8Route.get

`C8Route.get() : Response`

Performs a GET request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
Response response = route.get();
// response.getBody() is the response body of calling
// GET _db/_system/my-foxx-service

// -- or --

Response response = route.route("users").get();
// response.getBody() is the response body of calling
// GET _db/_system/my-foxx-service/users

// -- or --

Response response = route.route("users").withQueryParam("group", "admin").get();
// response.getBody() is the response body of calling
// GET _db/_system/my-foxx-service/users?group=admin
```

## C8Route.head

`C8Route.head() : Response`

Performs a HEAD request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route route = route.head();
// response is the response object for
// HEAD _db/_system/my-foxx-service
```

## C8Route.patch

`C8Route.patch() : Response`

Performs a PATCH request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route route = route.patch();
// response.getBody() is the response body of calling
// PATCH _db/_system/my-foxx-service

// -- or --

C8Route route = route.route("users/admin").patch();
// response.getBody() is the response body of calling
// PATCH _db/_system/my-foxx-service/users

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users/admin").withBody(body).patch();
// response.getBody() is the response body of calling
// PATCH _db/_system/my-foxx-service/users/admin
// with JSON request body {"password": "hunter2"}

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users/admin")
  .withBody(body).withQueryParam("admin", true).patch();
// response.getBody() is the response body of calling
// PATCH _db/_system/my-foxx-service/users/admin?admin=true
// with JSON request body {"password": "hunter2"}
```

## C8Route.post

`C8Route.post() : Response`

Performs a POST request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route route = route.post()
// response.getBody() is the response body of calling
// POST _db/_system/my-foxx-service

// -- or --

C8Route route = route.route("users").post()
// response.getBody() is the response body of calling
// POST _db/_system/my-foxx-service/users

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users").withBody(body).post();
// response.getBody() is the response body of calling
// POST _db/_system/my-foxx-service/users
// with JSON request body {"username": "admin", "password": "hunter2"}

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users")
  .withBody(body).withQueryParam("admin", true).post();
// response.getBody() is the response body of calling
// POST _db/_system/my-foxx-service/users?admin=true
// with JSON request body {"username": "admin", "password": "hunter2"}
```

## C8Route.put

`C8Route.put() : Response`

Performs a PUT request to the given URL and returns the server response.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Route route = db.route("my-foxx-service");
C8Route route = route.put();
// response.getBody() is the response body of calling
// PUT _db/_system/my-foxx-service

// -- or --

C8Route route = route.route("users/admin").put();
// response.getBody() is the response body of calling
// PUT _db/_system/my-foxx-service/users

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users/admin").withBody(body).put();
// response.getBody() is the response body of calling
// PUT _db/_system/my-foxx-service/users/admin
// with JSON request body {"username": "admin", "password": "hunter2"}

// -- or --

VPackSlice body = c8.util().serialize("{ password: 'hunter2' }");
C8Route route = route.route("users/admin")
  .withBody(body).withQueryParam("admin", true).put();
// response.getBody() is the response body of calling
// PUT _db/_system/my-foxx-service/users/admin?admin=true
// with JSON request body {"username": "admin", "password": "hunter2"}
```
