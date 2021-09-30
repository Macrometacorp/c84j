/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import java.util.List;

import lombok.Data;

@Data
public class TenantEntity {
	private String tenant;
	private List<String> dcList;
    private String status;
}
