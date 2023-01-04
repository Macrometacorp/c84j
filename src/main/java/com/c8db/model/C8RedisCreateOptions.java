/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */
package com.c8db.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class C8RedisCreateOptions {

    private Boolean stream;

}
