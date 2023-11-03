/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface IsSystemMixin<R> {
    String IS_SYSTEM = "isSystem";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    default boolean isSystem() {
        return getProperty(IS_SYSTEM) == Boolean.TRUE;
    }

    /**
     * @param system true if collection is a system (default: false)
     * @return {@link C8KVCreateBodyOptions}
     */
    default R system(final boolean system) {
        setProperty(IS_SYSTEM, system);
        return (R) this;
    }
}
