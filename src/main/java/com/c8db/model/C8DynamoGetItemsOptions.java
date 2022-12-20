/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class C8DynamoGetItemsOptions extends CollectionCreateOptions {

    private int limit;
    private Map<String, Object> exclusiveStartKey;

}
