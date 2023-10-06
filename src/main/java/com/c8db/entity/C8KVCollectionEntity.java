/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

public class C8KVCollectionEntity implements Entity {

    private String name;
    private boolean expiration;
    private boolean group;

    public C8KVCollectionEntity() {
    }

    public String getName() {
        return name;
    }

    public boolean hasExpiration() {
        return expiration;
    }

    public boolean hasGroup() {
        return group;
    }
}
