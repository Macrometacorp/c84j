/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import java.util.List;

import lombok.Data;

@Data
public class TenantsEntity {
    private String tenant;
    private List<String> dcList;
    private String status;
}
