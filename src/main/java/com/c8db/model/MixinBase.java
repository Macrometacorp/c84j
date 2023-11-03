/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.arangodb.velocypack.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * It is class for usage of mixin pattern.
 * Any class that uses mixins like `C8KVGroupMixin` needs to be extended from this class.
 * And Mixin should always have the next part:
 *  public interface NameMixin<R> {
 *     <T> T getProperty(String name);
 *     <T> void setProperty(String name, T value);
 *     ...
 *     default String getFoo() {
 *        getProperty("foo");
 *     }
 *     default R foo(String value) {
 *        setProperty("foo", value);
 *        return (R) this;
 *     }
 *  }
 */
public class MixinBase {

    @Expose(serialize = false)
    private final Map<String, Object> properties = new HashMap();

    /**
     * Get property to object of mixins
     *
     * @param name of the property
     * @return value of the property
     * @param <T> class of the property
     */
    public <T> T getProperty(String name) {
        Object value = properties.get(name);
        if (value != null) {
            return (T) value;
        }
        return null;
    }

    /**
     * Set property to object of mixins
     *
     * @param name of the property
     * @param value value of the property
     * @param <T> class of the property
     */
    public <T> void setProperty(String name, T value) {
        properties.put(name, value);
    }

    /**
     * Return list of properties set into object of mixins
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

}
