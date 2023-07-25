/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface C8KVPaginationMixin<R> {

    String OFFSET_PARAMETER = "offset";
    String LIMIT_PARAMETER = "limit";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param offset The offset of returned KVs
     * @return options
     */
    default R offset(final Integer offset) {
        setProperty(OFFSET_PARAMETER, offset);
        return (R) this;
    }

    /**
     * @return the offset of returned KVs
     */
    default int getOffset() {
        Integer offset = getProperty(OFFSET_PARAMETER);
        if (offset == null) {
            return 0;
        }
        return offset;
    }

    /**
     * @param limit The offset of returned KVs
     * @return options
     */
    default R limit(final Integer limit) {
        setProperty(LIMIT_PARAMETER, limit);
        return (R) this;
    }

    /**
     * @return the limit of returned KVs
     */
    default int getLimit() {
        Integer limit = getProperty(LIMIT_PARAMETER);
        if (limit == null) {
            return 20;
        }
        return limit;
    }


}
