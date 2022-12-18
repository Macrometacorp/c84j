/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.model;

public enum C8DynamoAttributeType {

    HASH("HASH"), RANGE("RANGE");

    private final String key;

    C8DynamoAttributeType(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static C8DynamoAttributeType fromKey(final String key) {
        for (final C8DynamoAttributeType cType : C8DynamoAttributeType.values()) {
            if (cType.key.equals(key)) {
                return cType;
            }
        }
        return null;
    }
}
