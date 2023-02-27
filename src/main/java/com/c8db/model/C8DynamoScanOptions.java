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
public class C8DynamoScanOptions {

    private Integer limit;
    private Map<String, Object> exclusiveStartKey;
    private String indexName;
    private String filterExpression;
    private Map<String, Object> expressionAttribute;

}
