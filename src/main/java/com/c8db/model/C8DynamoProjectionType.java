/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.model;

public enum C8DynamoProjectionType {

    ALL("ALL"), INCLUDE("INCLUDE"), KEYS_ONLY("KEYS_ONLY");

    private final String key;

    C8DynamoProjectionType(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static C8DynamoProjectionType fromKey(final String key) {
        for (final C8DynamoProjectionType cType : C8DynamoProjectionType.values()) {
            if (cType.key.equals(key)) {
                return cType;
            }
        }
        return null;
    }
}
