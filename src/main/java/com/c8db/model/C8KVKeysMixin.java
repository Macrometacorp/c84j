/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import java.util.Collection;
import java.util.List;

public interface C8KVKeysMixin<R> {

    String KEYS_PARAMETER = "keys";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param keys of a KV pairs.
     * @return options
     */
    default R keys(final Collection<String> keys) {
        setProperty(KEYS_PARAMETER, keys);
        return (R) this;
    }

    /**
     * @return the keys
     */
    default List<String> getKeys() {
        return getProperty(KEYS_PARAMETER);
    }

}
