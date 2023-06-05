/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import lombok.Data;

import java.util.Set;

/**
 * Attributes of a stream worker.
 */
@Data
public class C8StreamWorkerOptions {

    private String definition;
    private Set<String> regions;
    private String user;
    private Boolean isSystem;

}
