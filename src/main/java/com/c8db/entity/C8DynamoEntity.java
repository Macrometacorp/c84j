/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.List;

@Data
public class C8DynamoEntity implements Entity {
    private String tableName;
    private String tableSizeBytes;
    private String tableStatus;
    private long creationDateTime;
    private long itemCount;
	private List<DynamoAttributeDefinition> attributeDefinitions;
	private List<DynamoKeySchemaElement> keySchema;
}
