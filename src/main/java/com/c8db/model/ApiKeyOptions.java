/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */
package com.c8db.model;

import lombok.Getter;

public class ApiKeyOptions {
    @Getter
    private String apikey;

    public ApiKeyOptions() {
        super();
    }

    /**
     * @param apiKey The api key
     * @return options
     */
    protected ApiKeyOptions apiKey(final String apikey) {
        this.apikey = apikey;
        return this;
    }
}
