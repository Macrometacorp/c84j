/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;

import java.util.List;

public class C8DynamoCreateOptions extends CollectionCreateOptions{
    private List<DynamoAttributeDefinition> AttributeDefinitions;
    private String TableName;
    private List<DynamoKeySchemaElement> KeySchema;

    public List<DynamoAttributeDefinition> getAttributeDefinitionList() {
        return AttributeDefinitions;
    }

    public String getTableName() {
        return TableName;
    }

    public List<DynamoKeySchemaElement> getKeySchemaElementList() {
        return KeySchema;
    }

    public C8DynamoCreateOptions() {
        super();
    }

    public C8DynamoCreateOptions(String tableName) {
        this.TableName = tableName;
    }

    public C8DynamoCreateOptions(List<DynamoAttributeDefinition> attributeDefinitionList, String tableName, List<DynamoKeySchemaElement> keySchemaElementList) {
        this.AttributeDefinitions = attributeDefinitionList;
        this.TableName = tableName;
        this.KeySchema = keySchemaElementList;
    }

    public C8DynamoCreateOptions(C8DynamoCreateOptions options) {
        this.AttributeDefinitions = options.getAttributeDefinitionList();
        this.TableName = options.getTableName();
        this.KeySchema = options.getKeySchemaElementList();
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
                '}';
    }
}
