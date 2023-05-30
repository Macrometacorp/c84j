/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import lombok.Data;

import java.util.List;

/**
 * Attributes of a stream worker.
 */
@Data
public class C8StreamWorkerOptions {

    private String definition;
    private List<String> regions;
    private String user;
    private Boolean isSystem;

}
