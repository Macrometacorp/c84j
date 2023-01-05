/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

public enum CollectionModel {

    KV("KV"), DOCUMENT("DOC"), DYNAMO("DYNAMO"), REDIS("REDIS");

    private final String type;

    CollectionModel(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static CollectionModel fromType(final String type) {
        for (final CollectionModel cType : CollectionModel.values()) {
            if (cType.type.equals(type)) {
                return cType;
            }
        }
        return null;
    }
}
