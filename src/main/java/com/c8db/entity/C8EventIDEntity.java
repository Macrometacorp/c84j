/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import com.arangodb.velocypack.annotations.SerializedName;
import lombok.Data;

@Data
public class C8EventIDEntity implements Entity {

    @SerializedName("_id")
    private String id;
    @SerializedName("_key")
    private String key;
    @SerializedName("_rev")
    private String revision;
}
