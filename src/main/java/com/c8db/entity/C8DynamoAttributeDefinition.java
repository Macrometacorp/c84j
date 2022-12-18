/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import com.c8db.model.C8DynamoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Attribute definition for table
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class C8DynamoAttributeDefinition {

    @SerializedName("AttributeName")
    private String attributeName;
    @SerializedName("AttributeType")
    private C8DynamoType attributeType;

}
