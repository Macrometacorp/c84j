/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface C8KVGroupIdMixin<R> {

    String GROUP_ID_PARAMETER = "groupID";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param group The group of returned KVs
     * @return options
     */
    default R group(final String group) {
        setProperty(GROUP_ID_PARAMETER, group);
        return (R) this;
    }

    /**
     * @return the group of returned KVs
     */
    default String getGroup() {
        return getProperty(GROUP_ID_PARAMETER);
    }

}
