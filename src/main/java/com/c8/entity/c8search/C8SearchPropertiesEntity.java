/*
 * DISCLAIMER
 *
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

package com.c8.entity.c8search;

import java.util.Collection;

import com.c8.entity.ViewEntity;
import com.c8.entity.ViewType;

/**
 * 
 *
 */
public class C8SearchPropertiesEntity extends ViewEntity {

	private final C8SearchProperties properties;

	public C8SearchPropertiesEntity(final String id, final String name, final ViewType type,
		final C8SearchProperties properties) {
		super(id, name, type);
		this.properties = properties;
	}

	/**
	 * @return Wait at least this many milliseconds between committing index data changes and making them visible to
	 *         queries (default: 60000, to disable use: 0). For the case where there are a lot of inserts/updates, a
	 *         lower value, until commit, will cause the index not to account for them and memory usage would continue
	 *         to grow. For the case where there are a few inserts/updates, a higher value will impact performance and
	 *         waste disk space for each commit call without any added benefits.
	 */
	public Long getConsolidationIntervalMsec() {
		return properties.getConsolidationIntervalMsec();
	}

	/**
	 * @return Wait at least this many commits between removing unused files in data directory (default: 10, to disable
	 *         use: 0). For the case where the consolidation policies merge segments often (i.e. a lot of
	 *         commit+consolidate), a lower value will cause a lot of disk space to be wasted. For the case where the
	 *         consolidation policies rarely merge segments (i.e. few inserts/deletes), a higher value will impact
	 *         performance without any added benefits.
	 */
	public Long getCleanupIntervalStep() {
		return properties.getCleanupIntervalStep();
	}

	public ConsolidationPolicy getConsolidationPolicy() {
		return properties.getConsolidationPolicy();
	}

	/**
	 * @return A list of linked collections
	 */
	public Collection<CollectionLink> getLinks() {
		return properties.getLinks();
	}

}
