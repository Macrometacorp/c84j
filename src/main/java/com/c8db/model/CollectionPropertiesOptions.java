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
 */

package com.c8db.model;

/**
 */
public class CollectionPropertiesOptions {

	private Boolean waitForSync;
	private Boolean hasStream;
	private Boolean cacheEnabled;

	public CollectionPropertiesOptions() {
		super();
	}

	public Boolean getWaitForSync() {
		return waitForSync;
	}

	/**
	 * @param waitForSync
	 *            If true then creating or changing a document will wait until the data has been synchronized to disk.
	 * @return options
	 */
	public CollectionPropertiesOptions waitForSync(final Boolean waitForSync) {
		this.waitForSync = waitForSync;
		return this;
	}

	public Boolean getHasStream() {
		return hasStream;
	}

	/**
	 * @param hasStream True if a local stream is associated with the collection.
	 * @return options
	 */
	public CollectionPropertiesOptions hasStream(Boolean hasStream) {
		this.hasStream = hasStream;
		return this;
	}

	public Boolean getCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 * @param cacheEnabled True if collection has cache enabled.
	 * @return options
	 */
	public CollectionPropertiesOptions cacheEnabled(Boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
		return this;
	}
}
