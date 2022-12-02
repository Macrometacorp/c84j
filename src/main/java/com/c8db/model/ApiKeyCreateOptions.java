/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */
package com.c8db.model;

import lombok.Getter;

public class ApiKeyCreateOptions {

    @Getter
    private String keyid;

    public ApiKeyCreateOptions() {
        super();
    }

    /**
     * @param keyId Key id for the apiKey.
     * @return options.
     */
    protected ApiKeyCreateOptions keyId(final String keyId) {
        this.keyid = keyId;
        return this;
    }

}
