package com.c8db.model;

import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class C8DynamoCreateOptions {
    private List<DynamoAttributeDefinition> AttributeDefinitions;
    private String TableName;
    private List<DynamoKeySchemaElement> KeySchema;
    //private ObjectNode provisionedThroughput;

    public List<DynamoAttributeDefinition> getAttributeDefinitionList() {
        return AttributeDefinitions;
    }

    public String getTableName() {
        return TableName;
    }

    public List<DynamoKeySchemaElement> getKeySchemaElementList() {
        return KeySchema;
    }

    /*public ObjectNode getProvisionedThroughput() {
        return provisionedThroughput;
    }*/



    public C8DynamoCreateOptions() {
        super();
    }

    public C8DynamoCreateOptions(List<DynamoAttributeDefinition> attributeDefinitionList, String tableName, List<DynamoKeySchemaElement> keySchemaElementList, ObjectNode provisionedThroughput) {
        this.AttributeDefinitions = attributeDefinitionList;
        this.TableName = tableName;
        this.KeySchema = keySchemaElementList;
        //this.provisionedThroughput = provisionedThroughput;
    }

    public C8DynamoCreateOptions(C8DynamoCreateOptions options) {
        this.AttributeDefinitions = options.getAttributeDefinitionList();
        this.TableName = options.getTableName();
        this.KeySchema = options.getKeySchemaElementList();
        //this.provisionedThroughput = options.getProvisionedThroughput();
        //System.out.println("Set provisionedThroughput = " + provisionedThroughput);
    }
    protected C8DynamoCreateOptions tableName(final String tableName) {
        this.TableName = tableName;
        return this;
    }

    @Override
    public String toString() {
        return "C8DynamoCreateOptions{" +
                "attributeDefinitionList=" + AttributeDefinitions +
                ", tableName='" + TableName + '\'' +
                ", keySchemaElementList=" + KeySchema +
                //", provisionedThroughput=" + provisionedThroughput +
                '}';
    }
}
