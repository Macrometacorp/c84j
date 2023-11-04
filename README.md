# C8 Java SDK

> The C84J SDK for Java simpliﬁes use of GDN Services by providing a set of libraries that are consistent and familiar for Java developers. It provides support for API lifecycle consideration such as authentication, JWT rotation, data marshaling, and serialization.

## Compile Java SDK

```
mvn clean install -Dmaven.javadoc.skip=true -B
```

# Run Integration Tests

1. Configure `c8db.properties` file.
2. Set desired fabric in `BaseTest` class in `TEST_DB` property.
3. Run command for all tests
```shell
mvn test -DskipTests=false
```
or for selected:
```shell
mvn test -Dtest=C8CollectionStrongConsistencyTest,C8KVStrongConsistencyTest -DskipTests=false
```

## Reference

- [Reference](docs/Drivers/Java/Reference/README.md)
