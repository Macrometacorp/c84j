# Managing AQL user functions

These functions implement the
[HTTP API for managing AQL user functions](https://docs.c8db.com/latest/HTTP/AqlUserFunctions/index.html).

## C8Database.getAqlFunctions

`C8Database.getAqlFunctions(AqlFunctionGetOptions options) : Collection<AqlFunctionEntity>`

**Arguments**

- **options**: `AqlFunctionGetOptions`

  - **namespace**: `String`

    Returns all registered AQL user functions from namespace namespace

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
Collection<AqlFunctionEntity> functions = db.getAqlFunctions(
  new AqlFunctionGetOptions().namespace("myfuncs")
);
// functions is a list of function descriptions
```

## C8Database.createAqlFunction

`C8Database.createAqlFunction(String name, String code, AqlFunctionCreateOptions options) : void`

**Arguments**

- **name**: `String`

  A valid AQL function name, e.g.: `"myfuncs::accounting::calculate_vat"`

- **code**: `String`

  A String evaluating to a JavaScript function

- **options**: `AqlFunctionCreateOptions`

  - **isDeterministic**: `Boolean`

    An optional boolean value to indicate that the function results are fully
    deterministic (function return value solely depends on the input value
    and return value is the same for repeated calls with same input)

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.createAqlFunction("ACME::ACCOUNTING::CALCULATE_VAT",
                     "function (price) { return 0.19; }",
                     new AqlFunctionCreateOptions());
// Use the new function in an AQL query
String query = "FOR product IN products"
               + "RETURN MERGE("
               + "{vat: ACME::ACCOUNTING::CALCULATE_VAT(product.price)}, product)";
C8Cursor<Double> cursor = db.query(query, null, new AqlQueryOptions(), Double.class);
// cursor is a cursor for the query result
```

## C8Database.deleteAqlFunction

`C8Database.deleteAqlFunction(String name, AqlFunctionDeleteOptions options): Integer`

Deletes the AQL user function with the given name from the database.

**Arguments**

- **name**: `String`

  The name of the user function to delete

- **options**: `AqlFunctionDeleteOptions`

  - **group**: `Boolean`

    If set to true, then the function name provided in name is treated as a
    namespace prefix, and all functions in the specified namespace will be deleted.
    If set to false, the function name provided in name must be fully qualified,
    including any namespaces.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");
db.deleteAqlFunction("ACME::ACCOUNTING::CALCULATE_VAT", new AqlFunctionDeleteOptions());
// the function no longer exists
```
