/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved
 */

package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.Permissions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8ApiKeysTest extends BaseTest {

    private static final String STREAM_NAME = "dbCollectionTest";

    public C8ApiKeysTest(final Builder builder) {
        super(builder);
    }

    @Before
    public void setup() {

    }

    @After
    public void teardown() {
    }

    @Test
    public void getBacklog() {
        Permissions apiKeyStreamAccess = db.apiKeys().getStreamAccess("key", STREAM_NAME);
        assertThat(apiKeyStreamAccess, is(notNullValue()));
    }


    
    @AfterClass
    public static void shutdown() {
        db.drop();
    }
}
