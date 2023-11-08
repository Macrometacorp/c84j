/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseKeyValue;
import com.c8db.entity.C8KVCollectionEntity;
import com.c8db.entity.C8KVEntity;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.DatabaseMetadataEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.C8KVCountPairsOptions;
import com.c8db.model.C8KVCreateOptions;
import com.c8db.model.C8KVDeleteValueOptions;
import com.c8db.model.C8KVDeleteValuesOptions;
import com.c8db.model.C8KVDropOptions;
import com.c8db.model.C8KVReadKeysOptions;
import com.c8db.model.C8KVStoresReadOptions;
import com.c8db.model.C8KVInsertValuesOptions;
import com.c8db.model.C8KVReadValueOptions;
import com.c8db.model.C8KVReadValuesOptions;
import com.c8db.model.C8KVTruncateOptions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class C8KVStrongConsistencyTest extends BaseTest {

    private static String COLLECTION_NAME = "kv_test_2";
    private static String SOURCE_REGION_HEADER = "X-Gdn-Source-Region";
    private static String host;
    private static String spotDc;
    private static TestC8DBBuilder testC8DBBuilder;

    @Parameterized.Parameters
    public static Collection<C8DB.Builder> builders() {
        testC8DBBuilder = new TestC8DBBuilder();
        testC8DBBuilder.useProtocol(Protocol.HTTP_JSON);
        return Arrays.asList(testC8DBBuilder);
    }

    public C8KVStrongConsistencyTest(final Builder builder) {
        super(builder);
        host = builder.getHosts().iterator().next().getHost();
    }

    @Test
    public void test0_check_if_non_spot_host() {
        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        spotDc = metadataEntity.getOptions().getSpotDc();
        assertFalse("The host `" + host+ "` is the same as spot host `" + spotDc
                        + "`. This test suite requires connection to non-spot host for database " + TEST_DB,
                host.contains(spotDc));
    }

    @Test
    public void test1_create_KV_collection_with_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVCreateOptions options = new C8KVCreateOptions()
                // enable group for KV collection
                .strongConsistency(true)
                .waitForSync(true);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVEntity kvEntity = kvColl.create(options);
        assertEquals(kvEntity.getName(), COLLECTION_NAME);

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate that collection created with strong consistency by another call
        CollectionEntity coll2 = db.collection(COLLECTION_NAME).getInfo();
        assertEquals(coll2.getName(), COLLECTION_NAME);
        assertTrue(coll2.getStrongConsistency());
    }

    @Test
    public void  test2_fetchAll(){
        C8KVStoresReadOptions options = new C8KVStoresReadOptions()
                .strongConsistency(true);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        Collection<C8KVCollectionEntity> list = db.getKVStores(options);

        // validate
        assertTrue(list.stream().anyMatch(e -> e.getName().equals(COLLECTION_NAME)));
    }

    @Test
    public void test2_insert_KV_pairs_with_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        // set optional group parameter for pairs
        BaseKeyValue pair1 = new BaseKeyValue("key1", "value1", null);
        BaseKeyValue pair2 = new BaseKeyValue("key2", "value2", null);
        BaseKeyValue pair3 = new BaseKeyValue("key3", "value3", null);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVInsertValuesOptions options = new C8KVInsertValuesOptions()
                .strongConsistency(true);
        MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> result =
                kvColl.insertKVPairs(Arrays.asList(pair1, pair2, pair3), options);

        // validate
        String[] returnedKeys = result.getDocuments().stream().map(d ->d.getKey()).collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedKeys);
        assertArrayEquals(returnedKeys, new String[]{"key1", "key2", "key3"});
    }

    @Test
    public void test3_count_KV_pairs_with_strong_consistency() {
        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}

        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVCountPairsOptions options = new C8KVCountPairsOptions()
                .strongConsistency(true);
        long count = kvColl.countKVPairs(options);

        // validate
        assertEquals(count, 3);
    }

    @Test
    public void test3_get_KV_pair_with_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVReadValueOptions options = new C8KVReadValueOptions()
                .strongConsistency(true);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        BaseKeyValue pair = kvColl.getKVPair("key1", options);

        // validate
        assertEquals(pair.getKey(), "key1");
        assertEquals(pair.getValue(), "value1");
    }

    @Test
    public void test3_get_KV_keys_with_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVReadKeysOptions options = new C8KVReadKeysOptions()
                .strongConsistency(true);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        Collection<String> keys = kvColl.getKVKeys(options);

        // validate
        String[] keyArr = keys.toArray(new String[]{});
        Arrays.sort(keyArr);
        assertArrayEquals(keyArr, new String[]{"key1","key2", "key3"});
    }

    @Test
    public void test4_get_multiple_KV_pairs_with_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVReadValuesOptions options = new C8KVReadValuesOptions()
                .strongConsistency(true);
        MultiDocumentEntity<BaseKeyValue> pairs = kvColl.getKVPairs(options);

        // validate that all 3 pairs inserted
        assertEquals(pairs.getDocuments().size(), 3);
    }

    @Test
    public void test5_remove_KV_pair_by_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVDeleteValueOptions options = new C8KVDeleteValueOptions()
                .strongConsistency(true);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        kvColl.deleteKVPair("key1", options);

        // validate that the pair was deleted
        C8KVReadValueOptions options2 = new C8KVReadValueOptions()
                .strongConsistency(true);
        BaseKeyValue pair = kvColl.getKVPair("key1", options2);
        assertNull(pair);
    }

    @Test
    public void test6_remove_multiple_KV_pairs_by_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVDeleteValuesOptions options = new C8KVDeleteValuesOptions()
                .strongConsistency(true);
        kvColl.deleteKVPairs(Arrays.asList("key2"), options);

        // validate that only one pair left
        C8KVReadValuesOptions options2 = new C8KVReadValuesOptions()
                .strongConsistency(true);
        MultiDocumentEntity<BaseKeyValue> pairs = kvColl.getKVPairs(options2);
        assertEquals(pairs.getDocuments().size(), 1);
        assertEquals(pairs.getDocuments().iterator().next().getKey(), "key3");
    }

    @Test
    public void test7_truncate_KV_collection_by_strong_consistency() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVTruncateOptions options = new C8KVTruncateOptions().strongConsistency(true);
        kvColl.truncate(options);

        // validate that the last pair was deleted
        C8KVReadValuesOptions options2 = new C8KVReadValuesOptions()
                .strongConsistency(true);
        MultiDocumentEntity<BaseKeyValue> pairs = kvColl.getKVPairs(options2);
        assertEquals(pairs.getDocuments().size(), 0);
    }


    @Test
    public void test8_drop() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        C8KVDropOptions options = new C8KVDropOptions()
                .strongConsistency(true);
        kvColl.drop(options);
    }

}
