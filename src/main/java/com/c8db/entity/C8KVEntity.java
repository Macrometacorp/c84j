/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */
package com.c8db.entity;

public class C8KVEntity implements Entity {

	private String id;
	private String name;
	private Boolean waitForSync;
	private Boolean hasStream;

	public C8KVEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Boolean getWaitForSync() {
		return waitForSync;
	}

	public Boolean getHasStream() {
		return hasStream;
	}

}
