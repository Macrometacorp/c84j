/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

@Data
public class DynamoAttributeDefinition {
    private String  AttributeName;
    private String  AttributeType;
}
