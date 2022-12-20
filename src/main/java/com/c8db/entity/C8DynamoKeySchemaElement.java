/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import com.c8db.model.C8DynamoAttributeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Schema for creating dynamo table
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C8DynamoKeySchemaElement {
     @SerializedName("AttributeName")
     private String attributeName;
     @SerializedName("KeyType")
     private C8DynamoAttributeType keyType;
}
