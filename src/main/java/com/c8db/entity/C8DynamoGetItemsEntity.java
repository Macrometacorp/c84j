/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

/**
 * Result of retrieved items
 */
@Data
public class C8DynamoGetItemsEntity {

    Collection<Map<String, Object>> items;
    Map<String, Object> lastEvaluatedKey;
    long scannedCount;
    long count;

}
