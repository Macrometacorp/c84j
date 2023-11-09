/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

public class DatabaseMetadataEntity implements Entity {

    private String name;
    private Boolean isSystem;
    private MetadataOptions options;

    /**
     * @return the name of the database
     */
    public String getName() {
        return name;
    }

    /**
     * @return whether or not the database is the _system database
     */
    public Boolean isSystem() {
        return isSystem;
    }

    /**
     * @return options
     */
    public MetadataOptions getOptions() {
        return options;
    }

}
