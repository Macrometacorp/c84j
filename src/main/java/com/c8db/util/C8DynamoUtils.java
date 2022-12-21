/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.util;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.c8db.entity.C8DynamoAttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts Objects based on Dynamo JSON into original or vice versa
 */
public class C8DynamoUtils {

    public static Collection<Map<String, Object>> toOriginalListOfItems(Collection<Map<String, C8DynamoAttributeValue>> value) {
        Collection<Map<String, Object>> list = null;
        if (value != null) {
            list = new ArrayList<>();
            for(Map<String, C8DynamoAttributeValue> item : value) {
                list.add(toOriginalItem(item));
            }
        }
        return list;
    }

    public static Map<String, AttributeValue> toDynamoItem(Map<String, Object> value) {
        Map<String, AttributeValue> map = null;
        if (value != null) {
            map = new HashMap<>();
            for(Map.Entry<String, Object> entry : value.entrySet()) {
                map.put(entry.getKey(), ItemUtils.toAttributeValue(entry.getValue()));
            }
        }
        return map;
    }

    public static Map<String, Object> toOriginalItem(Map<String, C8DynamoAttributeValue> value) {
        Map<String, Object> map = null;
        if (value != null) {
            map = new HashMap<>();
            for(Map.Entry<String, C8DynamoAttributeValue> entry : value.entrySet()) {
                map.put(entry.getKey(), toOriginalAttribute(entry.getValue()));
            }
        }
        return map;
    }

    public static Object toOriginalAttribute(C8DynamoAttributeValue value) {
        if (value.getS() != null) {
            return value.getS();
        } else if (value.getN() != null) {
            return value.getN();
        } else if (value.getB() != null) {
            return value.getB();
        } else if (value.getSS() != null) {
            return value.getSS();
        } else if (value.getNS() != null) {
            return value.getNS();
        } else if (value.getBS() != null) {
            return value.getBS();
        } else if (value.getM() != null) {
            return toOriginalItem(value.getM());
        } else if (value.getL() != null) {
            List<Object> list = new ArrayList<>();
            for(C8DynamoAttributeValue attributeValue : value.getL()) {
                list.add(toOriginalAttribute(attributeValue));
            }
            return list;
        } else if (value.getNULL()) {
            return null;
        } else if (value.getBOOL()) {
            return value.getBOOL();
        }
        return null;
    }
}
