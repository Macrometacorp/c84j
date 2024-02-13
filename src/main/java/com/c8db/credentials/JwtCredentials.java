/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.credentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtCredentials implements C8Credentials {

    private String jwt;

    public JwtCredentials(String jwt) {
        this.jwt = jwt;
    }
}
