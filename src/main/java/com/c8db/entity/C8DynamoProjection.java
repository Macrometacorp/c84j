/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import com.c8db.model.C8DynamoAttributeType;
import com.c8db.model.C8DynamoProjectionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Projection of the secondary index
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C8DynamoProjection {
     @SerializedName("ProjectionType")
     private C8DynamoProjectionType projectionType;
     @SerializedName("NonKeyAttributes")
     private List<String> nonKeyAttributes;
}
