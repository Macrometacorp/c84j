/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.List;

@Data
public class C8DynamoEntity implements Entity {
    C8DynamoTableDescription TableDescription;

    @Data
    public static class C8DynamoTableDescription{
        private String TableName;
        private long TableSizeBytes;
        private String TableStatus;
        private long CreationDateTime;
        private long ItemCount;
        private List<DynamoAttributeDefinition> AttributeDefinitions;
        private List<DynamoKeySchemaElement> KeySchema;
    }
}
