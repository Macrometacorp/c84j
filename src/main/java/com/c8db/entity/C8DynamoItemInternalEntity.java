/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

/**
 * Result of retrieved item
 * Class for internal usage
 */
@Data
public class C8DynamoItemInternalEntity implements Entity {

    @SerializedName("ConsumedCapacity")
    C8DynamoConsumedCapacity consumedCapacity;

    @SerializedName("Item")
    Map<String, C8DynamoAttributeValue> item;

    @Data
    public static class C8DynamoConsumedCapacity {

        @SerializedName("TableName")
        private String tableName;
        @SerializedName("CapacityUnits")
        private long capacityUnits;

    }
}
