/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

@Data
public class ApiKeyCreateEntity {

    private String key;
    private String keyid;
    private String tenant;
    private String user;

}
