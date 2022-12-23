/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import com.c8db.entity.C8DynamoAttributeDefinition;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@Builder
@ToString
public class C8DynamoUpdateTableOptions {

    private Collection<C8DynamoAttributeDefinition> attributeDefinitions;
    private Collection<C8DynamoUpdateAction> globalSecondaryIndexes;

}
