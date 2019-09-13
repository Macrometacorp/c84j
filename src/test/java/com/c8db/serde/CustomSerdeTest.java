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

package com.c8db.serde;


import com.c8db.C8Collection;
import com.c8db.C8DB;
import com.c8db.C8Database;
import com.c8db.VelocyJack;
import com.c8db.entity.BaseDocument;
import com.c8db.internal.C8Defaults;
import com.c8db.model.DocumentCreateOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 */
public class CustomSerdeTest {

    private static String COLLECTION_NAME = "collection";

    private C8Database db;
    private C8Collection collection;

    @Before
    public void init() {
        VelocyJack velocyJack = new VelocyJack();
        velocyJack.configure((mapper) -> {
            mapper.configure(WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
            mapper.configure(USE_BIG_INTEGER_FOR_INTS, true);
        });
        C8DB arangoDB = new C8DB.Builder().serializer(velocyJack).build();

        String TEST_DB = "custom-serde-test";
        db = arangoDB.db(C8Defaults.DEFAULT_TENANT, TEST_DB);
        if (!db.exists()) {
            db.create();
        }

        collection = db.collection(COLLECTION_NAME);
        if (!collection.exists()) {
            collection.create();
        }
    }

    @After
    public void shutdown() {
        db.drop();
    }

    @Test
    public void c8qlSerialization() {
        String key = "test-" + UUID.randomUUID().toString();

        BaseDocument doc = new BaseDocument(key);
        doc.addAttribute("arr", Collections.singletonList("hello"));
        doc.addAttribute("int", 10);

        HashMap<String, Object> params = new HashMap<>();
        params.put("doc", doc);
        params.put("@collection", COLLECTION_NAME);

        BaseDocument result = db.query(
                "INSERT @doc INTO @@collection RETURN NEW",
                params,
                BaseDocument.class
        ).first();

        assertThat(result.getAttribute("arr"), instanceOf(String.class));
        assertThat(result.getAttribute("arr"), is("hello"));
        assertThat(result.getAttribute("int"), instanceOf(BigInteger.class));
        assertThat(result.getAttribute("int"), is(BigInteger.valueOf(10)));
    }

    @Test
    public void c8qlDeserialization() {
        String key = "test-" + UUID.randomUUID().toString();

        BaseDocument doc = new BaseDocument(key);
        doc.addAttribute("arr", Collections.singletonList("hello"));
        doc.addAttribute("int", 10);

        collection.insertDocument(doc, null);

        final BaseDocument result = db.query(
                "RETURN DOCUMENT(@docId)",
                Collections.singletonMap("docId", COLLECTION_NAME + "/" + key),
                BaseDocument.class
        ).first();

        assertThat(result.getAttribute("arr"), instanceOf(String.class));
        assertThat(result.getAttribute("arr"), is("hello"));
        assertThat(result.getAttribute("int"), instanceOf(BigInteger.class));
        assertThat(result.getAttribute("int"), is(BigInteger.valueOf(10)));
    }

    @Test
    public void insertDocument() {
        String key = "test-" + UUID.randomUUID().toString();

        BaseDocument doc = new BaseDocument(key);
        doc.addAttribute("arr", Collections.singletonList("hello"));
        doc.addAttribute("int", 10);

        BaseDocument result = collection.insertDocument(
                doc,
                new DocumentCreateOptions().returnNew(true)
        ).getNew();

        assertThat(result.getAttribute("arr"), instanceOf(String.class));
        assertThat(result.getAttribute("arr"), is("hello"));
        assertThat(result.getAttribute("int"), instanceOf(BigInteger.class));
        assertThat(result.getAttribute("int"), is(BigInteger.valueOf(10)));
    }

    @Test
    public void getDocument() {
        String key = "test-" + UUID.randomUUID().toString();

        BaseDocument doc = new BaseDocument(key);
        doc.addAttribute("arr", Collections.singletonList("hello"));
        doc.addAttribute("int", 10);

        collection.insertDocument(doc, null);

        final BaseDocument result = db.collection(COLLECTION_NAME).getDocument(
                key,
                BaseDocument.class,
                null);

        assertThat(result.getAttribute("arr"), instanceOf(String.class));
        assertThat(result.getAttribute("arr"), is("hello"));
        assertThat(result.getAttribute("int"), instanceOf(BigInteger.class));
        assertThat(result.getAttribute("int"), is(BigInteger.valueOf(10)));
    }

}
