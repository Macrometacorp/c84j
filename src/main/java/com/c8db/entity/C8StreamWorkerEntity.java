/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.Set;

@Data
public class C8StreamWorkerEntity {

    private String name;
    private String definition;
    private Set<String> regions;
    private boolean isActive;

}
