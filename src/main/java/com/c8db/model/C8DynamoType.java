/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.model;

public enum C8DynamoType {

    STRING("S"), NUMBER("N"), BINARY("B");

    private final String key;

    C8DynamoType(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static C8DynamoType fromKey(final String key) {
        for (final C8DynamoType cType : C8DynamoType.values()) {
            if (cType.key.equals(key)) {
                return cType;
            }
        }
        return null;
    }
}
