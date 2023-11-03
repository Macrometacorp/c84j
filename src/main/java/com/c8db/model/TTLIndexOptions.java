/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.c8db.entity.IndexType;

/**
 * Index option for Time to live (ttl) index type.
 */
public class TTLIndexOptions extends IndexOptions<TTLIndexOptions> {

    private Iterable<String> fields;
    private long expireAfter;

    public TTLIndexOptions() {
        super(IndexType.ttl);
    }

    protected Iterable<String> getFields() {
        return fields;
    }

    /**
     * @param fields A list of attribute paths
     * @return options
     */
    protected TTLIndexOptions fields(final Iterable<String> fields) {
        this.fields = fields;
        return this;
    }

    public Long getExpireAfter() {
        return expireAfter;
    }

    /**
     * @param expireAfter Number of seconds to be added to the timestamp attribute value of each document.
     *                    If documents have reached their expiration timepoint,
     *                    they will eventually get deleted by a background process.
     * @return options
     */
    public TTLIndexOptions expireAfter(final Long expireAfter) {
        this.expireAfter = expireAfter;
        return this;
    }

}
