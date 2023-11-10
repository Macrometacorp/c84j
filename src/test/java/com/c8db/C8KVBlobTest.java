/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.BaseKeyValue;
import com.c8db.entity.BlobKeyValue;
import com.c8db.entity.C8KVEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.C8KVCreateOptions;
import com.c8db.model.C8KVReadValuesOptions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class C8KVBlobTest extends BaseTest {

    private static String COLLECTION_NAME = "kv_test_blob";

    public C8KVBlobTest(final Builder builder) {
        super(builder);
    }


    @Test
    public void test1_create_KV_collection() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVCreateOptions options = new C8KVCreateOptions()
                // enable blobs
                .blobs(true)
                .waitForSync(true);
        C8KVEntity kvEntity = kvColl.create(options);

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate
        assertEquals(kvEntity.getName(), COLLECTION_NAME);
    }

    @Test
    public void test2_get_not_exiting_KV_pairs() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVReadValuesOptions options = new C8KVReadValuesOptions().keys(Arrays.asList("key1"));
        MultiDocumentEntity<BlobKeyValue> result = kvColl.getBlobKVPairs(options);

        assertEquals(result.getDocuments().size(), 0);
    }

    @Test
    public void test3_insert_blob_KV_pairs() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        // set group parameter for pairs
        BlobKeyValue pair1 = new BlobKeyValue("key1", "value1".getBytes(StandardCharsets.UTF_8), null);
        BlobKeyValue pair2 = new BlobKeyValue("key2", "value2".getBytes(StandardCharsets.UTF_8), null);
        MultiDocumentEntity<DocumentCreateEntity<BlobKeyValue>> result =
                kvColl.insertBlobKVPairs(Arrays.asList(pair1, pair2));

        // validate
        String[] returnedKeys = result.getDocuments().stream().map(d ->d.getKey()).collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedKeys);
        assertArrayEquals(returnedKeys, new String[]{"key1","key2"});
    }

    @Test
    public void test3_insert_base_KV_pairs() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        // set group parameter for pairs
        BaseKeyValue pair3 = new BaseKeyValue("key3", "value3", null);
        MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> result = kvColl.insertKVPairs(Arrays.asList(pair3));

        // validate
        String[] returnedKeys = result.getDocuments().stream().map(d ->d.getKey()).collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedKeys);
        assertArrayEquals(returnedKeys, new String[]{"key3"});
    }

    @Test
    public void test4_get_blob_and_base_KV_pairs() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVReadValuesOptions options = new C8KVReadValuesOptions().keys(Arrays.asList("key1","key2","key3"));
        MultiDocumentEntity<BlobKeyValue> result = kvColl.getBlobKVPairs(options);

        // validate
        String[] returnedKeys = result.getDocuments().stream().map(BaseDocument::getKey)
                .collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedKeys);
        assertArrayEquals(returnedKeys, new String[]{"key1","key2","key3"});

        String[] returnedValues =
                result.getDocuments().stream().map(d -> new String(d.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedValues);
        assertArrayEquals(returnedValues, new String[]{"value1","value2","value3"});
    }

    @Test
    public void test4_get_blob_pair() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        BlobKeyValue result = kvColl.getBlobKVPair("key1");
        assertEquals("value1", new String(result.getValue()));
        assertEquals("key1", result.getKey());
    }

    @Test
    public void test4_get_blob_as_base_pair() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        BaseKeyValue result = kvColl.getKVPair("key1");
        assertEquals("Blob data type", result.getValue());
        assertEquals("key1", result.getKey());
    }

    @Test
    public void test4_get_base_as_blob_pair() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        BlobKeyValue result = kvColl.getBlobKVPair("key3");
        assertEquals("value3", new String(result.getValue()));
        assertEquals("key3", result.getKey());
    }
    
    @Test
    public void test5_drop() {
        db.kv(COLLECTION_NAME).drop();
    }

}
