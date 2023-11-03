/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.runners.Parameterized.Parameters;

import com.c8db.entity.ServerRole;
import com.c8db.internal.C8Defaults;

/**
 */
public abstract class BaseTest {
public abstract class BaseTest extends Assert {

    @Parameters
    public static Collection<C8DB.Builder> builders() {
        return Arrays.asList(//
                // new ArangoDB.Builder().useProtocol(Protocol.VST), //
                new C8DB.Builder().useProtocol(Protocol.HTTP_JSON)// , //
        // new ArangoDB.Builder().useProtocol(Protocol.HTTP_VPACK) //
        );
    }

    protected static final String TEST_DB = "javaDriverTestDb";
    protected static final String TEST_DB_CUSTOM = "javaDriverTestDbCustom";
    protected static C8DB c8DB;
    protected static C8Database db;

    public BaseTest(final C8DB.Builder builder) {
        super();
        if (c8DB != null) {
            shutdown();
        }
        c8DB = builder.build();
        db = c8DB.db(C8Defaults.DEFAULT_TENANT, TEST_DB);

        // only create the database if not existing
        try {
            db.getVersion().getVersion();
        } catch (final C8DBException e) {
            if (e.getErrorNum() == 1228) { // DATABASE NOT FOUND
                c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, TEST_DB, "", C8Defaults.DEFAULT_DC_LIST, TEST_DB);
            }
        }
    }

    @AfterClass
    public static void shutdown() {
        c8DB.shutdown();
        c8DB = null;
    }

    protected boolean requireVersion(final int major, final int minor) {
        final String[] split = c8DB.getVersion().getVersion().split("\\.");
        return Integer.valueOf(split[0]) >= major && Integer.valueOf(split[1]) >= minor;
    }

    protected boolean requireSingleServer() {
        return (c8DB.getRole() == ServerRole.SINGLE);
    }

}
