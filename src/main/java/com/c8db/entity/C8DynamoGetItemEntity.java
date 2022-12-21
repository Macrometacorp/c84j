/*
 *  Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import lombok.Data;

import java.util.Map;

/**
 * Result of retrieved item
 */
@Data
public class C8DynamoGetItemEntity {

    C8DynamoItemInternalEntity.C8DynamoConsumedCapacity consumedCapacity;
    Map<String, Object> item;

}
