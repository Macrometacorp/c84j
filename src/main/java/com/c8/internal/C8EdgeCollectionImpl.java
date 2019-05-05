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
import com.c8.C8EdgeCollection;
import com.c8.entity.EdgeEntity;
import com.c8.entity.EdgeUpdateEntity;
import com.c8.model.DocumentReadOptions;
import com.c8.model.EdgeCreateOptions;
import com.c8.model.EdgeDeleteOptions;
import com.c8.model.EdgeReplaceOptions;
import com.c8.model.EdgeUpdateOptions;

/**
 * 
 *
 */
public class C8EdgeCollectionImpl
		extends InternalC8EdgeCollection<C8DBImpl, C8DatabaseImpl, C8GraphImpl, C8ExecutorSync>
		implements C8EdgeCollection {

	private static final Logger LOGGER = LoggerFactory.getLogger(C8EdgeCollectionImpl.class);

	protected C8EdgeCollectionImpl(final C8GraphImpl graph, final String name) {
		super(graph, name);
	}

	@Override
	public <T> EdgeEntity insertEdge(final T value) throws C8DBException {
		return executor.execute(insertEdgeRequest(value, new EdgeCreateOptions()),
			insertEdgeResponseDeserializer(value));
	}

	@Override
	public <T> EdgeEntity insertEdge(final T value, final EdgeCreateOptions options) throws C8DBException {
		return executor.execute(insertEdgeRequest(value, options), insertEdgeResponseDeserializer(value));
	}

	@Override
	public <T> T getEdge(final String key, final Class<T> type) throws C8DBException {
		try {
			return executor.execute(getEdgeRequest(key, new DocumentReadOptions()), getEdgeResponseDeserializer(type));
		} catch (final C8DBException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage(), e);
			}
			return null;
		}
	}

	@Override
	public <T> T getEdge(final String key, final Class<T> type, final DocumentReadOptions options)
			throws C8DBException {
		try {
			return executor.execute(getEdgeRequest(key, options), getEdgeResponseDeserializer(type));
		} catch (final C8DBException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage(), e);
			}
			return null;
		}
	}

	@Override
	public <T> EdgeUpdateEntity replaceEdge(final String key, final T value) throws C8DBException {
		return executor.execute(replaceEdgeRequest(key, value, new EdgeReplaceOptions()),
			replaceEdgeResponseDeserializer(value));
	}

	@Override
	public <T> EdgeUpdateEntity replaceEdge(final String key, final T value, final EdgeReplaceOptions options)
			throws C8DBException {
		return executor.execute(replaceEdgeRequest(key, value, options), replaceEdgeResponseDeserializer(value));
	}

	@Override
	public <T> EdgeUpdateEntity updateEdge(final String key, final T value) throws C8DBException {
		return executor.execute(updateEdgeRequest(key, value, new EdgeUpdateOptions()),
			updateEdgeResponseDeserializer(value));
	}

	@Override
	public <T> EdgeUpdateEntity updateEdge(final String key, final T value, final EdgeUpdateOptions options)
			throws C8DBException {
		return executor.execute(updateEdgeRequest(key, value, options), updateEdgeResponseDeserializer(value));
	}

	@Override
	public void deleteEdge(final String key) throws C8DBException {
		executor.execute(deleteEdgeRequest(key, new EdgeDeleteOptions()), Void.class);
	}

	@Override
	public void deleteEdge(final String key, final EdgeDeleteOptions options) throws C8DBException {
		executor.execute(deleteEdgeRequest(key, options), Void.class);
	}

}
