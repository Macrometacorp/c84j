/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import com.c8db.model.C8DynamoAttributeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * Schema for creating dynamo table
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C8DynamoSecondaryIndex {

     @SerializedName("IndexName")
     private String indexName;
     @SerializedName("KeySchema")
     private Collection<C8DynamoKeySchemaElement> keySchema;
     @SerializedName("Projection")
     private C8DynamoProjection projection;

}
