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

import java.util.Iterator;

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPack.SerializeOptions;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DBException;
import com.c8.util.C8Serializer;

/**
 * 
 *
 */
public class C8SerializerImpl implements C8Serializer {

	private final VPack vpacker;
	private final VPack vpackerNull;
	private final VPackParser vpackParser;

	public C8SerializerImpl(final VPack vpacker, final VPack vpackerNull, final VPackParser vpackParser) {
		super();
		this.vpacker = vpacker;
		this.vpackerNull = vpackerNull;
		this.vpackParser = vpackParser;
	}

	@Override
	public VPackSlice serialize(final Object entity) throws C8DBException {
		return serialize(entity, new C8Serializer.Options());
	}

	@SuppressWarnings("unchecked")
	@Override
	public VPackSlice serialize(final Object entity, final Options options) throws C8DBException {
		if (options.getType() == null) {
			options.type(entity.getClass());
		}
		try {
			final VPackSlice vpack;
			final Class<? extends Object> type = entity.getClass();
			final boolean serializeNullValues = options.isSerializeNullValues();
			if (String.class.isAssignableFrom(type)) {
				vpack = vpackParser.fromJson((String) entity, serializeNullValues);
			} else if (options.isStringAsJson() && Iterable.class.isAssignableFrom(type)) {
				final Iterator<?> iterator = Iterable.class.cast(entity).iterator();
				if (iterator.hasNext() && String.class.isAssignableFrom(iterator.next().getClass())) {
					vpack = vpackParser.fromJson((Iterable<String>) entity, serializeNullValues);
				} else {
					final VPack vp = serializeNullValues ? vpackerNull : vpacker;
					vpack = vp.serialize(entity,
						new SerializeOptions().type(options.getType()).additionalFields(options.getAdditionalFields()));
				}
			} else {
				final VPack vp = serializeNullValues ? vpackerNull : vpacker;
				vpack = vp.serialize(entity,
					new SerializeOptions().type(options.getType()).additionalFields(options.getAdditionalFields()));
			}
			return vpack;
		} catch (final VPackException e) {
			throw new C8DBException(e);
		}
	}

}
