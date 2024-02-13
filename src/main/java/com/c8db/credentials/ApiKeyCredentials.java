/**
 * Copyright (c) 2024 Macrometa Corp All rights reserved.
 */
package com.c8db.credentials;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiKeyCredentials implements C8Credentials {

    private String apiKey;

    public ApiKeyCredentials(String apiKey) {
        this.apiKey = apiKey;
    }
}
