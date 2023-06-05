/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */
package com.c8db.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Attributes of an Api Key.
 */
@Data
@AllArgsConstructor
public class ApiKeyCreateOptions {

    private String keyid;
    private String user;
    private Boolean isSystem;

}
