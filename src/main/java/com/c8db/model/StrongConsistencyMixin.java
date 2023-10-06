/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface StrongConsistencyMixin<R> {

    String STRONG_CONSISTENCY = "strongConsistency";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @return if a collection is strongly consistent
     */
    default boolean hasStrongConsistency() {
        return getProperty(STRONG_CONSISTENCY) == Boolean.TRUE;
    }

    /**
     * @param strongConsistency Enable strong consistency (default: false)
     * @return options
     */
    default R strongConsistency(final boolean strongConsistency) {
        setProperty(STRONG_CONSISTENCY, strongConsistency);
        return (R) this;
    }

}
