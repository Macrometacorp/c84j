/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import lombok.Data;

/**
 * Attributes of a secret.
 */
@Data
public class C8SecretOptions {
    
    private String name;
    private String value;
    private String fileName;
    private String keyName;

}
