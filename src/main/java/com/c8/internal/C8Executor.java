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

import java.lang.reflect.Type;

import com.arangodb.velocypack.exception.VPackException;
import com.c8.internal.util.C8SerializationFactory;
import com.c8.internal.util.C8SerializationFactory.Serializer;
import com.c8.util.C8Serialization;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public abstract class C8Executor {

	public static interface ResponseDeserializer<T> {
		T deserialize(Response response) throws VPackException;
	}

	private final DocumentCache documentCache;
	private final C8Serialization util;

	protected C8Executor(final C8SerializationFactory util, final DocumentCache documentCache) {
		super();
		this.documentCache = documentCache;
		this.util = util.get(Serializer.INTERNAL);
	}

	public DocumentCache documentCache() {
		return documentCache;
	}

	@SuppressWarnings("unchecked")
	protected <T> T createResult(final Type type, final Response response) {
		return (T) ((type != Void.class && response.getBody() != null) ? util.deserialize(response.getBody(), type)
				: null);
	}

}
