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

import java.util.Collection;

import com.c8.C8DBException;
import com.c8.C8EdgeCollection;
import com.c8.C8Graph;
import com.c8.C8VertexCollection;
import com.c8.entity.EdgeDefinition;
import com.c8.entity.GraphEntity;
import com.c8.model.GraphCreateOptions;

/**
 * 
 *
 */
public class C8GraphImpl extends InternalC8Graph<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
		implements C8Graph {

	protected C8GraphImpl(final C8DatabaseImpl db, final String name) {
		super(db, name);
	}

	@Override
	public boolean exists() throws C8DBException {
		try {
			getInfo();
			return true;
		} catch (final C8DBException e) {
			if (C8Errors.ERROR_GRAPH_NOT_FOUND.equals(e.getErrorNum())) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public GraphEntity create(final Collection<EdgeDefinition> edgeDefinitions) throws C8DBException {
		return db().createGraph(name(), edgeDefinitions);
	}

	@Override
	public GraphEntity create(final Collection<EdgeDefinition> edgeDefinitions, final GraphCreateOptions options)
			throws C8DBException {
		return db().createGraph(name(), edgeDefinitions, options);
	}

	@Override
	public void drop() throws C8DBException {
		executor.execute(dropRequest(), Void.class);
	}

	@Override
	public void drop(final boolean dropCollections) throws C8DBException {
		executor.execute(dropRequest(dropCollections), Void.class);
	}

	@Override
	public GraphEntity getInfo() throws C8DBException {
		return executor.execute(getInfoRequest(), getInfoResponseDeserializer());
	}

	@Override
	public Collection<String> getVertexCollections() throws C8DBException {
		return executor.execute(getVertexCollectionsRequest(), getVertexCollectionsResponseDeserializer());
	}

	@Override
	public GraphEntity addVertexCollection(final String name) throws C8DBException {
		return executor.execute(addVertexCollectionRequest(name), addVertexCollectionResponseDeserializer());
	}

	@Override
	public C8VertexCollection vertexCollection(final String name) {
		return new C8VertexCollectionImpl(this, name);
	}

	@Override
	public C8EdgeCollection edgeCollection(final String name) {
		return new C8EdgeCollectionImpl(this, name);
	}

	@Override
	public Collection<String> getEdgeDefinitions() throws C8DBException {
		return executor.execute(getEdgeDefinitionsRequest(), getEdgeDefinitionsDeserializer());
	}

	@Override
	public GraphEntity addEdgeDefinition(final EdgeDefinition definition) throws C8DBException {
		return executor.execute(addEdgeDefinitionRequest(definition), addEdgeDefinitionResponseDeserializer());
	}

	@Override
	public GraphEntity replaceEdgeDefinition(final EdgeDefinition definition) throws C8DBException {
		return executor.execute(replaceEdgeDefinitionRequest(definition), replaceEdgeDefinitionResponseDeserializer());
	}

	@Override
	public GraphEntity removeEdgeDefinition(final String definitionName) throws C8DBException {
		return executor.execute(removeEdgeDefinitionRequest(definitionName),
			removeEdgeDefinitionResponseDeserializer());
	}

}
