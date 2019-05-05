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

package com.c8.model.c8search;

import com.c8.entity.ViewType;
import com.c8.entity.c8search.C8SearchProperties;
import com.c8.entity.c8search.CollectionLink;
import com.c8.entity.c8search.ConsolidationPolicy;

/**
 * 
 *
 */
public class C8SearchCreateOptions {

	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private final ViewType type;
	private final C8SearchProperties properties;

	public C8SearchCreateOptions() {
		super();
		type = ViewType.c8_SEARCH;
		properties = new C8SearchProperties();
	}

	protected C8SearchCreateOptions name(final String name) {
		this.name = name;
		return this;
	}

	/**
	 * @param consolidationIntervalMsec
	 *            Wait at least this many milliseconds between committing index data changes and making them visible to
	 *            queries (default: 60000, to disable use: 0). For the case where there are a lot of inserts/updates, a
	 *            lower value, until commit, will cause the index not to account for them and memory usage would
	 *            continue to grow. For the case where there are a few inserts/updates, a higher value will impact
	 *            performance and waste disk space for each commit call without any added benefits.
	 * @return options
	 */
	public C8SearchCreateOptions consolidationIntervalMsec(final Long consolidationIntervalMsec) {
		properties.setConsolidationIntervalMsec(consolidationIntervalMsec);
		return this;
	}

	/**
	 * @param cleanupIntervalStep
	 *            Wait at least this many commits between removing unused files in data directory (default: 10, to
	 *            disable use: 0). For the case where the consolidation policies merge segments often (i.e. a lot of
	 *            commit+consolidate), a lower value will cause a lot of disk space to be wasted. For the case where the
	 *            consolidation policies rarely merge segments (i.e. few inserts/deletes), a higher value will impact
	 *            performance without any added benefits.
	 * @return options
	 */
	public C8SearchCreateOptions cleanupIntervalStep(final Long cleanupIntervalStep) {
		properties.setCleanupIntervalStep(cleanupIntervalStep);
		return this;
	}

	/**
	 * @param consolidationPolicy
	 * 
	 * @return options
	 */
	public C8SearchCreateOptions consolidationPolicy(final ConsolidationPolicy consolidationPolicy) {
		properties.setConsolidationPolicy(consolidationPolicy);
		return this;
	}

	/**
	 * @param links
	 *            A list of linked collections
	 * @return options
	 */
	public C8SearchCreateOptions link(final CollectionLink... links) {
		properties.addLink(links);
		return this;
	}
}
