/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface C8KVGroupMixin {

    String GROUP_PARAMETER = "group";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param group The group of returned KVs
     * @return options
     */
    default C8KVGroupMixin group(final String group) {
        setProperty(GROUP_PARAMETER, group);
        return this;
    }

    /**
     * @return the group of returned KVs
     */
    default String getGroup() {
        return getProperty(GROUP_PARAMETER);
    }

}
