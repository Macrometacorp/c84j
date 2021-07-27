/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.example;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.c8db.C8Collection;
import com.c8db.C8DB;
import com.c8db.C8DBException;
import com.c8db.C8Database;
import com.c8db.internal.C8Defaults;

/**
 *
 */
public class ExampleBase {

    protected static final String DB_NAME = "json_example_db";
    protected static final String COLLECTION_NAME = "json_example_collection";

    protected static C8DB c8DB;
    protected static C8Database db;
    protected static C8Collection collection;

    @BeforeClass
    public static void setUp() {
        c8DB = new C8DB.Builder().build();
        try {
            c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop();
        } catch (final C8DBException e) {
        }
        c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME, "", C8Defaults.DEFAULT_DC_LIST, DB_NAME);
        db = c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME);
        db.createCollection(COLLECTION_NAME);
        collection = db.collection(COLLECTION_NAME);
    }

    @AfterClass
    public static void tearDown() {
        db.drop();
        c8DB.shutdown();
    }

}
