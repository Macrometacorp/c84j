package com.c8db.model;

import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class C8DynamoCreateOptions {
    private List<DynamoAttributeDefinition> attributeDefinitionList;
    private String tableName;
    private List<DynamoKeySchemaElement> keySchemaElementList;
    private ObjectNode provisionedThroughput;

    public C8DynamoCreateOptions() {
        super();
    }

    public C8DynamoCreateOptions(List<DynamoAttributeDefinition> attributeDefinitionList, String tableName, List<DynamoKeySchemaElement> keySchemaElementList, ObjectNode provisionedThroughput) {
        this.attributeDefinitionList = attributeDefinitionList;
        this.tableName = tableName;
        this.keySchemaElementList = keySchemaElementList;
        this.provisionedThroughput = provisionedThroughput;
    }

    protected C8DynamoCreateOptions tableName(final String tableName) {
        this.tableName = tableName;
        return this;
    }
}
