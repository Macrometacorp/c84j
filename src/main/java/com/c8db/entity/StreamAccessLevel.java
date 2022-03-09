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

    NO_ACCESS("none", 0), READ("ro", 1), READ_WRITE("rw", 2);

    private final String levelName;
    private final int level;

    StreamAccessLevel(final String levelName, int level) {
        this.levelName = levelName;
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getLevel() {
        return level;
    }

    public static StreamAccessLevel fromLevelName(final String levelName) {
        for (final StreamAccessLevel streamAccessLevel : StreamAccessLevel.values()) {
            if (streamAccessLevel.levelName.equalsIgnoreCase(levelName)) {
                return streamAccessLevel;
            }
        }
        return null;
    }

}
