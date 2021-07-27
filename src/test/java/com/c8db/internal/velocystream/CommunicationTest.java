/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.internal.velocystream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.c8db.C8DB;
import com.c8db.C8DBException;
import com.c8db.C8Database;
import com.c8db.entity.C8DBVersion;
import com.c8db.internal.C8Defaults;

/**
 *
 */
public class CommunicationTest {

    private static final String FAST = "fast";
    private static final String SLOW = "slow";

    @Test
    public void chunkSizeSmall() {
        final C8DB arangoDB = new C8DB.Builder().chunksize(20).build();
        final C8DBVersion version = arangoDB.getVersion();
        assertThat(version, is(notNullValue()));
    }

    @Test
    public void multiThread() throws Exception {
        final C8DB arangoDB = new C8DB.Builder().build();
        arangoDB.getVersion();// authentication

        final Collection<String> result = new ConcurrentLinkedQueue<String>();
        final Thread fast = new Thread() {
            @Override
            public void run() {
                try {
                    arangoDB.db().query("return sleep(1)", null, null, null);
                    result.add(FAST);
                } catch (final C8DBException e) {
                }
            }
        };
        final Thread slow = new Thread() {
            @Override
            public void run() {
                try {
                    arangoDB.db().query("return sleep(4)", null, null, null);
                    result.add(SLOW);
                } catch (final C8DBException e) {
                }
            }
        };
        slow.start();
        Thread.sleep(1000);
        fast.start();

        slow.join();
        fast.join();

        assertThat(result.size(), is(2));
        final Iterator<String> iterator = result.iterator();
        assertThat(iterator.next(), is(FAST));
        assertThat(iterator.next(), is(SLOW));
    }

    @Test
    public void multiThreadSameDatabases() throws Exception {
        final C8DB arangoDB = new C8DB.Builder().build();
        arangoDB.getVersion();// authentication

        final C8Database db = arangoDB.db();

        final Collection<String> result = new ConcurrentLinkedQueue<String>();
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    db.query("return sleep(1)", null, null, null);
                    result.add("1");
                } catch (final C8DBException e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        final Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    db.query("return sleep(1)", null, null, null);
                    result.add("1");
                } catch (final C8DBException e) {
                    e.printStackTrace(System.err);
                }
            }
        };
        t2.start();
        t1.start();
        t2.join();
        t1.join();
        assertThat(result.size(), is(2));
    }

    @Test
    public void multiThreadMultiDatabases() throws Exception {
        final C8DB arangoDB = new C8DB.Builder().build();
        arangoDB.getVersion();// authentication

        try {
            arangoDB.createGeoFabric(C8Defaults.DEFAULT_TENANT, "db1", "", C8Defaults.DEFAULT_DC_LIST, "db1");
            arangoDB.createGeoFabric(C8Defaults.DEFAULT_TENANT, "db2", "", C8Defaults.DEFAULT_DC_LIST, "db1");
            final C8Database db1 = arangoDB.db(C8Defaults.DEFAULT_TENANT, "db1");
            final C8Database db2 = arangoDB.db(C8Defaults.DEFAULT_TENANT, "db2");

            final Collection<String> result = new ConcurrentLinkedQueue<String>();
            final Thread t1 = new Thread() {
                @Override
                public void run() {
                    try {
                        db1.query("return sleep(1)", null, null, null);
                        result.add("1");
                    } catch (final C8DBException e) {
                    }
                }
            };
            final Thread t2 = new Thread() {
                @Override
                public void run() {
                    try {
                        db2.query("return sleep(1)", null, null, null);
                        result.add("1");
                    } catch (final C8DBException e) {
                    }
                }
            };
            t2.start();
            t1.start();
            t2.join();
            t1.join();
            assertThat(result.size(), is(2));
        } finally {
            arangoDB.db(C8Defaults.DEFAULT_TENANT, "db1").drop();
            arangoDB.db(C8Defaults.DEFAULT_TENANT, "db2").drop();
        }
    }

    @Test
    public void minOneConnection() {
        final C8DB arangoDB = new C8DB.Builder().maxConnections(0).build();
        final C8DBVersion version = arangoDB.getVersion();
        assertThat(version, is(notNullValue()));
    }

    @Test
    public void defaultMaxConnection() {
        final C8DB arangoDB = new C8DB.Builder().maxConnections(null).build();
        final C8DBVersion version = arangoDB.getVersion();
        assertThat(version, is(notNullValue()));
    }
}
