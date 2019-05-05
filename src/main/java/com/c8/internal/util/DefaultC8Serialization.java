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

package com.c8.internal.util;

import java.lang.reflect.Type;

import com.arangodb.velocypack.VPackSlice;
import com.c8.C8DBException;
import com.c8.util.C8Deserializer;
import com.c8.util.C8Serialization;
import com.c8.util.C8Serializer;

/**
 * 
 *
 */
public class DefaultC8Serialization implements C8Serialization {

	private final C8Serializer serializer;
	private final C8Deserializer deserializer;

	public DefaultC8Serialization(final C8Serializer serializer, final C8Deserializer deserializer) {
		super();
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public VPackSlice serialize(final Object entity) throws C8DBException {
		return serializer.serialize(entity);
	}

	@Override
	public VPackSlice serialize(final Object entity, final Options options) throws C8DBException {
		return serializer.serialize(entity, options);
	}

	@Override
	public <T> T deserialize(final VPackSlice vpack, final Type type) throws C8DBException {
		return deserializer.deserialize(vpack, type);
	}

}
