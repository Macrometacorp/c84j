/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.List;

@Data
public class C8DynamoDescribeEntity implements Entity {
    C8DynamoTableDescription Table;

    @Data
    public static class C8DynamoTableDescription{
        private String TableName;
        private String TableStatus;
        private String TableArn;
        private long ItemCount;
        private long CreationDateTime;
        private List<DynamoAttributeDefinition> AttributeDefinitions;
        private List<DynamoKeySchemaElement> KeySchema;
    }
}
