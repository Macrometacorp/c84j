/*
 *  Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

/**
 * Result of deleted table
 */
@Data
public class C8DynamoDeleteTableEntity {

    @SerializedName("TableDescription")
    C8DynamoCreateTableEntity.C8DynamoTableDescription tableDescription;

    @Data
    public static class C8DynamoTableDescription {

        @SerializedName("TableName")
        private String tableName;
        @SerializedName("TableStatus")
        private String tableStatus;
        @SerializedName("TableArn")
        private long tableArn;

    }
}
