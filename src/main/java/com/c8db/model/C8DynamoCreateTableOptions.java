/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import com.c8db.entity.C8DynamoAttributeDefinition;
import com.c8db.entity.C8DynamoKeySchemaElement;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class C8DynamoCreateTableOptions {
    private List<C8DynamoAttributeDefinition> attributeDefinitions;
    private List<C8DynamoKeySchemaElement> keySchema;
}
