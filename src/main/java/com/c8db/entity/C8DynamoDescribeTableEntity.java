/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

import java.util.Collection;

/**
 * Result describes table
 */
@Data
public class C8DynamoDescribeTableEntity implements Entity {

    @SerializedName("Table")
    C8DynamoTableDescription table;

    @Data
    public static class C8DynamoTableDescription {

        @SerializedName("TableName")
        private String tableName;
        @SerializedName("TableSizeBytes")
        private long tableSizeBytes;
        @SerializedName("TableStatus")
        private String tableStatus;
        @SerializedName("TableArn")
        private String tableArn;
        @SerializedName("CreationDateTime")
        private long creationDateTime;
        @SerializedName("ItemCount")
        private long itemCount;
        @SerializedName("AttributeDefinitions")
        private Collection<C8DynamoAttributeDefinition> attributeDefinitions;
        @SerializedName("KeySchema")
        private Collection<C8DynamoKeySchemaElement> keySchema;
        @SerializedName("GlobalSecondaryIndexes")
        private Collection<C8DynamoSecondaryIndex> globalSecondaryIndexes;
        @SerializedName("LocalSecondaryIndexes")
        private Collection<C8DynamoSecondaryIndex> localSecondaryIndexes;

    }
}
