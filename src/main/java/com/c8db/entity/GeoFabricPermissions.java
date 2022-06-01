/**
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import java.util.Map;

/**
 * This class has full list of permissions for GeoFabric, collections and streams.
 */
public class GeoFabricPermissions {

    private Permissions permission;

    private Map<String, Permissions> collections;

    private Map<String, Permissions> streams;

    public Permissions getPermission() {
        return permission;
    }

    public Map<String, Permissions> getCollections() {
        return collections;
    }

    public Map<String, Permissions> getStreams() {
        return streams;
    }
}
