/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.Set;

@Data
public class C8SecretEntity {

    private String name;
    private String value;
    private String tenant;
    private String fabric;
    private String fileName;
    private String keyName;
    private String keyId;
    private Long createdAt;
    private Long updatedAt;

}
