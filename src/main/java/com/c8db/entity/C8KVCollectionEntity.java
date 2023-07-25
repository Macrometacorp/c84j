/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class C8KVCollectionEntity implements Entity {
	private String name;
	private boolean expiration;
	private boolean group;
}
