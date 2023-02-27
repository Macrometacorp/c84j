/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Attribute value
 */
@Data
public class C8DynamoAttributeValue {

    private String S;
    private String N;
    private ByteBuffer B;
    private List<String> SS;
    private List<String> NS;
    private List<ByteBuffer> BS;
    private Map<String, C8DynamoAttributeValue> M;
    private List<C8DynamoAttributeValue> L;
    private Boolean NULL;
    private Boolean BOOL;

}
