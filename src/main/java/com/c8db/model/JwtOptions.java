/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */
package com.c8db.model;

import lombok.Getter;

public class JwtOptions {
    @Getter
    private String jwt;

    public JwtOptions() {
        super();
    }

    /**
     * @param jwt The JWT
     * @return options
     */
    protected JwtOptions jwt(final String jwt) {
        this.jwt = jwt;
        return this;
    }
}
