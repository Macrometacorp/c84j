/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */

package com.c8db.entity;

/**
 * StreamAccessLevel created for getting access level of a stream
 */
public enum StreamAccessLevel {

    NO_ACCESS("none"), READ("ro"), READ_WRITE("rw");

    private final String level;

    StreamAccessLevel(final String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public static StreamAccessLevel fromLevel(final String level) {
        for (final StreamAccessLevel streamAccessLevel : StreamAccessLevel.values()) {
            if (streamAccessLevel.level.equalsIgnoreCase(level)) {
                return streamAccessLevel;
            }
        }
        return null;
    }
}
