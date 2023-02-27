/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

/**
 * Result of retrieved items
 * Class for internal usage
 */
@Data
public class C8DynamoItemsInternalEntity implements Entity {

    @SerializedName("Items")
    Collection<Map<String, C8DynamoAttributeValue>> items;
    @SerializedName("LastEvaluatedKey")
    Map<String, C8DynamoAttributeValue> lastEvaluatedKey;
    @SerializedName("ScannedCount")
    long scannedCount;
    @SerializedName("Count")
    long count;

}
