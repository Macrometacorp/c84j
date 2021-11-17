/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import lombok.Data;

@Data
public class ApiKeyEntity {
    private String keyid;
    private String tenant;
    private String parent;
    private int limitRequestsPerSec;
}
