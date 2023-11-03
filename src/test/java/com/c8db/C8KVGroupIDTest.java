/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseKeyValue;
import com.c8db.entity.C8KVCollectionEntity;
import com.c8db.entity.C8KVEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.C8KVCountPairsOptions;
import com.c8db.model.C8KVCreateOptions;
import com.c8db.model.C8KVReadGroupsOptions;
import com.c8db.model.C8KVReadKeysOptions;
import com.c8db.model.C8KVTruncateOptions;
import com.c8db.model.C8KVUpdateGroupOptions;
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
public class C8KVGroupIDTest extends BaseTest {

    private static String COLLECTION_NAME = "kv_test_1";
    private static String GROUP_A = "groupA";
    private static String GROUP_B = "groupB";
    private static String GROUP_C = "groupC";


    public C8KVGroupIDTest(final Builder builder) {
        super(builder);
    }

    @Test
    public void test1_create_KV_collection_with_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVCreateOptions options = new C8KVCreateOptions()
                // enable group for KV collection
                .group(true)
                .strongConsistency(true)
                .waitForSync(true);
        C8KVEntity kvEntity = kvColl.create(options);

        // validate
        assertEquals(kvEntity.getName(), COLLECTION_NAME);
    }

    @Test
    public void test2_insert_KV_pairs_with_groups() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        // set group parameter for pairs
        BaseKeyValue pair1 = new BaseKeyValue("key1", "value1", null, GROUP_A);
        BaseKeyValue pair2 = new BaseKeyValue("key2", "value2", null, GROUP_B);
        BaseKeyValue pair3 = new BaseKeyValue("key3", "value3", null, GROUP_B);
        MultiDocumentEntity<DocumentCreateEntity<BaseKeyValue>> result = kvColl.insertKVPairs(Arrays.asList(pair1, pair2, pair3));

        // validate
        String[] returnedKeys = result.getDocuments().stream().map(d ->d.getKey()).collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(returnedKeys);
        assertArrayEquals(returnedKeys, new String[]{"key1", "key2", "key3"});
    }

    @Test
    public void test3_count_KV_pairs_by_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVCountPairsOptions options = new C8KVCountPairsOptions()
                // added parameter group, which return count of pairs in the groupB
                .group(GROUP_B);
        long count = kvColl.countKVPairs(options);

        // validate
        assertEquals(count, 2);
    }

    @Test
    public void test4_get_KV_keys_by_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVReadKeysOptions options = new C8KVReadKeysOptions()
                // added optional parameter group, which return count of pairs in the groupB
                .group(GROUP_B);
        Collection<String> keys = kvColl.getKVKeys(options);

        // validate
        String[] keyArr = keys.toArray(new String[]{});
        Arrays.sort(keyArr);
        assertArrayEquals(keyArr, new String[]{"key2", "key3"});
    }

    @Test
    public void test5_get_KV_pair_with_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        BaseKeyValue pair = kvColl.getKVPair("key1");

        //validate
        assertEquals(pair.getKey(), "key1");
        // returns with group
        assertEquals(pair.getGroupID(), GROUP_A);
    }

    @Test
    public void test6_get_list_KV_collections_with_group() {
        Collection<C8KVCollectionEntity> list = db.getKVStores();

        // check if group enabled for collection
        C8KVCollectionEntity entity = list.stream().filter(e ->
                e.getName().equals(COLLECTION_NAME)).findFirst().get();
        assertTrue(entity.hasGroup());
    }

    @Test
    public void test7_get_groups() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        Collection<String> groups = kvColl.getGroups();

        // validate
        String[] groupArr = groups.toArray(new String[]{});
        Arrays.sort(groupArr);
        assertArrayEquals(groupArr, new String[]{GROUP_A, GROUP_B});
    }

    @Test
    public void test7_get_groups_with_options() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVReadGroupsOptions options = new C8KVReadGroupsOptions().strongConsistency(true);
        Collection<String> groups = kvColl.getGroups(options);

        // validate
        String[] groupArr = groups.toArray(new String[]{});
        Arrays.sort(groupArr);
        assertArrayEquals(groupArr, new String[]{GROUP_A, GROUP_B});
    }

    @Test
    public void test7_update_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);

        C8KVUpdateGroupOptions options = new C8KVUpdateGroupOptions().strongConsistency(true);
        kvColl.updateGroup(GROUP_A, GROUP_C, options);

        // validate
        C8KVReadGroupsOptions options2 = new C8KVReadGroupsOptions().strongConsistency(true);
        Collection<String> groups = kvColl.getGroups(options2);

        // validate
        String[] groupArr = groups.toArray(new String[]{});
        Arrays.sort(groupArr);
        assertArrayEquals(groupArr, new String[]{GROUP_B, GROUP_C});
    }

    @Test
    public void test8_truncate_KV_collection_by_group() {
        C8KeyValue kvColl = db.kv(COLLECTION_NAME);
        C8KVTruncateOptions options = new C8KVTruncateOptions().group(GROUP_B);
        kvColl.truncate(options);

        // validate that only groupB was deleted
        Collection<String> keys = kvColl.getKVKeys();
        // check if group enabled for collection
        assertArrayEquals(keys.toArray(), new String[]{"key1"});
    }
    
    @Test
    public void test9_drop() {
        db.kv(COLLECTION_NAME).drop();
    }


}
