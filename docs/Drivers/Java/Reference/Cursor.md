# Cursor API

_C8Cursor_ instances provide an abstraction over the HTTP API's limitations.
Unless a method explicitly exhausts the cursor, the driver will only fetch as
many batches from the server as necessary. Like the server-side cursors,
_C8Cursor_ instances are incrementally depleted as they are read from.

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query(
  "FOR x IN 1..5 RETURN x", Integer.class
);
// query result list: [1, 2, 3, 4, 5]
Integer value = cursor.next();
assertThat(value, is(1));
// remaining result list: [2, 3, 4, 5]
```

## C8Cursor.hasNext

`C8Cursor.hasNext() : boolean`

Returns _true_ if the cursor has more elements in its current batch of results
or the cursor on the server has more batches.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
boolean hasNext = cursor.hasNext();
```

## C8Cursor.next

`C8Cursor.next() : T`

Returns the next element of the query result. If the current element is the last
element of the batch and the cursor on the server provides more batches, the
next batch is fetched from the server.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Integer value = cursor.next();
assertThat(value, is(1));
```

## C8Cursor.first

`C8Cursor.first() : T`

Returns the first element or {@code null} if no element exists.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("RETURN 1", Integer.class)
Integer value = cursor.first();
assertThat(value, is(1));
```

## C8Cursor.foreach

`C8Cursor.foreach(Consumer<? super T> action) : void`

Performs the given action for each element of the _C8Iterable_

**Arguments**

- **action**: `Consumer<? super T>`

  A action to perform on the elements

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
cursor.foreach(e -> {
  // remaining results: [1, 2, 3, 4, 5]
});
```

## C8Cursor.map

`C8Cursor.map(Function<? super T, ? extends R> mapper) : C8Iterable<R>`

Returns a _C8Iterable_ consisting of the results of applying the given
function to the elements of this _C8Iterable_.

**Arguments**

- **mapper**: `Function<? super T, ? extends R>`

  A function to apply to each element

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
cursor.map(e -> e * 10).foreach(e -> {
  // remaining results: [10, 20, 30, 40, 50]
});
```

## C8Cursor.filter

`C8Cursor.filter(Predicate<? super T> predicate) : C8Iterable<T>`

**Arguments**

- **predicate**: `Predicate<? super T>`

  A predicate to apply to each element to determine if it should be included

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
cursor.filter(e -> e < 4).foreach(e -> {
  // remaining results: [1, 2, 3]
});
```

## C8Cursor.anyMatch

`C8Cursor.anyMatch(Predicate<? super T> predicate) : boolean`

Returns whether any elements of this _C8Iterable_ match the provided predicate.

**Arguments**

- **predicate**: `Predicate<? super T>`

  A predicate to apply to elements of this {@code C8Iterable}

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
boolean match = cursor.anyMatch(e -> e == 3);
assertThat(match, is(true));
```

## C8Cursor.allMatch

`C8Cursor.anyMatch(Predicate<? super T> predicate) : boolean`

Returns whether all elements of this _C8Iterable_ match the provided predicate.

**Arguments**

- **predicate**: `Predicate<? super T>`

  A predicate to apply to elements of this {@code C8Iterable}

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
boolean match = cursor.allMatch(e -> e <= 5);
assertThat(match, is(true));
```

## C8Cursor.noneMatch

`C8Cursor.noneMatch(Predicate<? super T> predicate) : boolean`

Returns whether no elements of this _C8Iterable_ match the provided predicate.

**Arguments**

- **predicate**: `Predicate<? super T>`

  A predicate to apply to elements of this {@code C8Iterable}

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
boolean match = cursor.noneMatch(e -> e > 5);
assertThat(match, is(true));
```

## C8Cursor.collectInto

`C8Cursor.collectInto(R target) : R`

**Arguments**

Iterates over all elements of this {@code C8Iterable} and adds each to
the given target.

- **target**: `R <R extends Collection<? super T>>`

  The collection to insert into

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Collection<Integer> list = cursor.collectInto(new ArrayList());
// -- or --
Collection<Integer> set = cursor.collectInto(new HashSet());
```

## C8Cursor.iterator

`C8Cursor.iterator() : Iterator<T>`

Returns an iterator over elements of the query result.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Iterator<Integer> iterator = cursor.iterator();
```

## C8Cursor.asListRemaining

`C8Cursor.asListRemaining() : List<T>`

Returns the remaining results as a _List_.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Collection<Integer> list = cursor.asListRemaining();
```

## C8Cursor.getCount

`C8Cursor.getCount() : Integer`

Returns the total number of result documents available (only available if the
query was executed with the _count_ attribute set)

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", new AqlQueryOptions().count(true), Integer.class)
Integer count = cursor.getCount();
assertThat(count, is(5));
```

## C8Cursor.count

`C8Cursor.count() : long`

Returns the count of elements of this _C8Iterable_.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
long count = cursor.filter(e -> e < 4).count();
// remaining results: [1, 2, 3]
assertThat(count, is(3L));
```

## C8Cursor.getStats

`C8Cursor.getStats() : Stats`

Returns extra information about the query result. For data-modification queries,
the stats will contain the number of modified documents and the number of
documents that could not be modified due to an error (if `ignoreErrors`
query option is specified).

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Stats stats = cursor.getStats();
```

## C8Cursor.getWarnings

`C8Cursor.getWarnings() : Collection<Warning>`

Returns warnings which the query could have been produced.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
Collection<Warning> warnings = cursor.getWarnings();
```

## C8Cursor.isCached

`C8Cursor.isCached() : boolean`

Indicating whether the query result was served from the query cache or not.

**Examples**

```Java
C8DB c8 = new C8DB.Builder().build();
C8Database db = c8.db("myDB");

C8Cursor<Integer> cursor = db.query("FOR x IN 1..5 RETURN x", Integer.class)
boolean cached = cursor.isCached();
```
