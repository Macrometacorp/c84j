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

package com.c8.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.c8.C8DBException;
import com.c8.C8VertexCollection;
import com.c8.entity.VertexEntity;
import com.c8.entity.VertexUpdateEntity;
import com.c8.model.DocumentReadOptions;
import com.c8.model.VertexCreateOptions;
import com.c8.model.VertexDeleteOptions;
import com.c8.model.VertexReplaceOptions;
import com.c8.model.VertexUpdateOptions;

/**
 * 
 *
 */
public class C8VertexCollectionImpl
		extends InternalC8VertexCollection<C8DBImpl, C8DatabaseImpl, C8GraphImpl, C8ExecutorSync>
		implements C8VertexCollection {

	private static final Logger LOGGER = LoggerFactory.getLogger(C8VertexCollectionImpl.class);

	protected C8VertexCollectionImpl(final C8GraphImpl graph, final String name) {
		super(graph, name);
	}

	@Override
	public void drop() throws C8DBException {
		executor.execute(dropRequest(), Void.class);
	}

	@Override
	public <T> VertexEntity insertVertex(final T value) throws C8DBException {
		return executor.execute(insertVertexRequest(value, new VertexCreateOptions()),
			insertVertexResponseDeserializer(value));
	}

	@Override
	public <T> VertexEntity insertVertex(final T value, final VertexCreateOptions options) throws C8DBException {
		return executor.execute(insertVertexRequest(value, options), insertVertexResponseDeserializer(value));
	}

	@Override
	public <T> T getVertex(final String key, final Class<T> type) throws C8DBException {
		try {
			return executor.execute(getVertexRequest(key, new DocumentReadOptions()),
				getVertexResponseDeserializer(type));
		} catch (final C8DBException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage(), e);
			}
			return null;
		}
	}

	@Override
	public <T> T getVertex(final String key, final Class<T> type, final DocumentReadOptions options)
			throws C8DBException {
		try {
			return executor.execute(getVertexRequest(key, options), getVertexResponseDeserializer(type));
		} catch (final C8DBException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage(), e);
			}
			return null;
		}
	}

	@Override
	public <T> VertexUpdateEntity replaceVertex(final String key, final T value) throws C8DBException {
		return executor.execute(replaceVertexRequest(key, value, new VertexReplaceOptions()),
			replaceVertexResponseDeserializer(value));
	}

	@Override
	public <T> VertexUpdateEntity replaceVertex(final String key, final T value, final VertexReplaceOptions options)
			throws C8DBException {
		return executor.execute(replaceVertexRequest(key, value, options), replaceVertexResponseDeserializer(value));
	}

	@Override
	public <T> VertexUpdateEntity updateVertex(final String key, final T value) throws C8DBException {
		return executor.execute(updateVertexRequest(key, value, new VertexUpdateOptions()),
			updateVertexResponseDeserializer(value));
	}

	@Override
	public <T> VertexUpdateEntity updateVertex(final String key, final T value, final VertexUpdateOptions options)
			throws C8DBException {
		return executor.execute(updateVertexRequest(key, value, options), updateVertexResponseDeserializer(value));
	}

	@Override
	public void deleteVertex(final String key) throws C8DBException {
		executor.execute(deleteVertexRequest(key, new VertexDeleteOptions()), Void.class);
	}

	@Override
	public void deleteVertex(final String key, final VertexDeleteOptions options) throws C8DBException {
		executor.execute(deleteVertexRequest(key, options), Void.class);
	}

}
