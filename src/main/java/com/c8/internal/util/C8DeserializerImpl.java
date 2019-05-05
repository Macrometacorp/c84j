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

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DBException;
import com.c8.util.C8Deserializer;

/**
 * @author Mark Vollmary
 *
 */
public class C8DeserializerImpl implements C8Deserializer {

	private final VPack vpacker;
	private final VPackParser vpackParser;

	public C8DeserializerImpl(final VPack vpacker, final VPackParser vpackParser) {
		super();
		this.vpacker = vpacker;
		this.vpackParser = vpackParser;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(final VPackSlice vpack, final Type type) throws C8DBException {
		try {
			final T doc;
			if (type == String.class && !vpack.isString()) {
				doc = (T) vpackParser.toJson(vpack, true);
			} else {
				doc = vpacker.deserialize(vpack, type);
			}
			return doc;
		} catch (final VPackException e) {
			throw new C8DBException(e);
		}
	}
}
