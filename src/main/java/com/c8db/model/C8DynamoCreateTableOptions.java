/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import com.c8db.entity.C8DynamoAttributeDefinition;
import com.c8db.entity.C8DynamoSecondaryIndex;
import com.c8db.entity.C8DynamoKeySchemaElement;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Builder
@ToString
public class C8DynamoCreateTableOptions {

    private Collection<C8DynamoAttributeDefinition> attributeDefinitions;
    private Collection<C8DynamoKeySchemaElement> keySchema;
    private Collection<C8DynamoSecondaryIndex> globalSecondaryIndexes;
    private Collection<C8DynamoSecondaryIndex> localSecondaryIndexes;

}
