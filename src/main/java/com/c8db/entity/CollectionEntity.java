/*
 * DISCLAIMER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.c8db.entity;

/**
 */
public class CollectionEntity implements Entity {

	private String id;
	private String name;
	private Boolean waitForSync;
	private Boolean isSpot;
	private Boolean isSystem;
	private CollectionStatus status;
	private CollectionType type;
	private Boolean hasStream;
	private CollectionModel collectionModel;

	public CollectionEntity() {
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

	public Boolean getIsSpot() {
		return isSpot;
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public CollectionStatus getStatus() {
		return status;
	}

	public CollectionType getType() {
		return type;
	}

	public Boolean getHasStream() {
		return hasStream;
	}

	public CollectionModel getCollectionModel() {
		return collectionModel;
	}

}
