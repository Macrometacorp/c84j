/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public class C8KVPairReadOptions {

    private String offset;
    private String limit;

    public C8KVPairReadOptions() {
        super();
        offset = String.valueOf(0);
        limit = String.valueOf(20);
    }

    public String getOffset() {
        return offset;
    }

    /**
     * @param offset The offset of returned KVs
     * @return options
     */
    public C8KVPairReadOptions offset(final String offset) {
        this.offset = offset;
        return this;
    }

    public String getLimit() {
        return limit;
    }

    /**
     * @param limit returned KVs
     * @return options
     */
    public C8KVPairReadOptions limit(final String limit) {
        this.limit = limit;
        return this;
    }

}
