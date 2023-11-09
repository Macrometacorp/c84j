/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.DatabaseMetadataEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.Map;

/**
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class GeoFabricsTest extends BaseTest {

    private static Map<String, Object> initialMetadata;
    private static final String TEST_KEY_1 = "test_key_1";
    private static final String TEST_VALUE_1 = "test_value_1";

    private static final String TEST_KEY_2 = "test_key_2";
    private static final String TEST_VALUE_2 = "test_value_2";

    public GeoFabricsTest(final Builder builder) {
        super(builder);
    }

    @Test
    public void test1_get_metadata() {
        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        initialMetadata = metadataEntity.getOptions().getMetadata();

        // validate
        assertEquals(metadataEntity.getName(), TEST_DB);
    }

    @Test
    public void test2_set_test_metadata() {
        //set test metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TEST_KEY_1, TEST_VALUE_1);
        db.setMetadata(metadata);

        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        // validate
        assertEquals(metadataEntity.getOptions().getMetadata().size(), 1);
        assertEquals(metadataEntity.getOptions().getMetadata().get(TEST_KEY_1), TEST_VALUE_1);
    }

    @Test
    public void test3_set_again_metadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TEST_KEY_2, TEST_VALUE_2);
        db.setMetadata(metadata);

        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        // validate
        assertEquals(metadataEntity.getOptions().getMetadata().size(), 1);
        assertEquals(metadataEntity.getOptions().getMetadata().get(TEST_KEY_2), TEST_VALUE_2);
    }

    @Test
    public void test4_update_metadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TEST_KEY_1, TEST_VALUE_1);
        db.updateMetadata(metadata);

        // validate
        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        assertEquals(metadataEntity.getOptions().getMetadata().size(), 2);
        assertEquals(metadataEntity.getOptions().getMetadata().get(TEST_KEY_1), TEST_VALUE_1);
        assertEquals(metadataEntity.getOptions().getMetadata().get(TEST_KEY_2), TEST_VALUE_2);
    }

    @Test
    public void test5_return_original_metadata() {
        db.setMetadata(initialMetadata);
    }

}
