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
public class C8DynamoBatchWriteItemInternalEntity implements Entity {

    @SerializedName("UnprocessedItems")
    private Map<String, Collection<C8DynamoPutRequest>> unprocessedItems;

    //{"UnprocessedItems":{"dyn-test-10":[{"PutRequest":{"Item":{"part":{"S":"10"},"value":{"S":"val122"}}}}]}}

    @Data
    public static class C8DynamoPutRequest {

        @SerializedName("PutRequest")
        private C8DynamoItem putRequest;

    }

    @Data
    public static class C8DynamoItem {

        @SerializedName("Item")
        Map<String, C8DynamoAttributeValue> item;

    }

}
