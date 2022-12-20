/*
 *
 *  Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

/**
 * Result for PutItem and UpdateItem the same.
 */
@Data
public class C8DynamoBatchWriteItemEntity implements Entity {

    @SerializedName("UnprocessedItems")
    private Map<String, Collection<Map<String, Object>>> unprocessedItems;

}
