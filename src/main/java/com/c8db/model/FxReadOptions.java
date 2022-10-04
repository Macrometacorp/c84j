/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.c8db.entity.FxType;

/**
 * Read options for function worker
 */
public class FxReadOptions {

    private FxType type = FxType.ALL;

    public FxReadOptions() {
        super();
    }

    public FxType getType() {
        return type;
    }

    /**
     * Set type of function.
     *
     * @param type - instance of `FxType` class.
     * @return
     */
    public FxReadOptions type(final FxType type) {
        this.type = type;
        return this;
    }
}
