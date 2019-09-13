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

package com.c8db;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

import com.c8db.jackson.dataformat.velocypack.VPackMapper;
import com.c8db.util.C8Serialization;
import com.c8db.util.C8Serializer;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public class VelocyJack implements C8Serialization {

    public interface ConfigureFunction {
        void configure(ObjectMapper mapper);
    }

    private final ObjectMapper vpackMapper;
    private final ObjectMapper vpackMapperNull;
    private final ObjectMapper jsonMapper;
    private final VPackParser vpackParser;

    public VelocyJack() {
        super();
        vpackMapper = new VPackMapper().setSerializationInclusion(Include.NON_NULL);
        vpackMapperNull = new VPackMapper().setSerializationInclusion(Include.ALWAYS);
        jsonMapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
        vpackParser = new VPackParser.Builder().build();
    }

    public void configure(final ConfigureFunction f) {
        f.configure(vpackMapper);
        f.configure(vpackMapperNull);
        f.configure(jsonMapper);
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
                    final ObjectMapper vp = serializeNullValues ? vpackMapperNull : vpackMapper;
                    vpack = new VPackSlice(vp.writeValueAsBytes(entity));
                }
            } else {
                final ObjectMapper vp = serializeNullValues ? vpackMapperNull : vpackMapper;
                vpack = new VPackSlice(vp.writeValueAsBytes(entity));
            }
            return vpack;
        } catch (final JsonProcessingException e) {
            throw new C8DBException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final VPackSlice vpack, final Type type) throws C8DBException {
        try {
            final T doc;
            if (type == String.class && !vpack.isString()) {
                final JsonNode node = vpackMapper.readTree(Arrays.copyOfRange(vpack.getBuffer(), vpack.getStart(),
                        vpack.getStart() + vpack.getByteSize()));
                doc = (T) jsonMapper.writeValueAsString(node);
            } else {
                doc = vpackMapper.readValue(vpack.getBuffer(), vpack.getStart(), vpack.getStart() + vpack.getByteSize(),
                        (Class<T>) type);
            }
            return doc;
        } catch (final IOException e) {
            throw new C8DBException(e);
        }
    }

}
