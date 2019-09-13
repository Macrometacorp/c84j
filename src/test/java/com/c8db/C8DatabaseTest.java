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

package com.c8db;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import com.arangodb.velocypack.exception.VPackException;
import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.BaseEdgeDocument;
import com.c8db.entity.C8DBVersion;
import com.c8db.entity.C8StreamEntity;
import com.c8db.entity.C8qlExecutionExplainEntity;
import com.c8db.entity.C8qlParseEntity;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.CollectionType;
import com.c8db.entity.DatabaseEntity;
import com.c8db.entity.GraphEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.PathEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.QueryEntity;
import com.c8db.entity.QueryExecutionState;
import com.c8db.entity.QueryTrackingPropertiesEntity;
import com.c8db.entity.TraversalEntity;
import com.c8db.entity.UserQueryEntity;
import com.c8db.entity.UserQueryOptions;
import com.c8db.entity.C8qlExecutionExplainEntity.ExecutionPlan;
import com.c8db.internal.C8Defaults;
import com.c8db.model.C8TransactionOptions;
import com.c8db.model.C8qlQueryOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.CollectionsReadOptions;
import com.c8db.model.TraversalOptions;
import com.c8db.model.TraversalOptions.Direction;
import com.c8db.util.MapBuilder;

/**
 * 
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8DatabaseTest extends BaseTest {

    Logger LOG = LoggerFactory.getLogger(C8DatabaseTest.class);

    private static final String COLLECTION_NAME = "dbtest";
    private static final String GRAPH_NAME = "graph_test";
    private static final String STREAM_NAME = "streamtest";

    public C8DatabaseTest(final Builder builder) {
        super(builder);
    }

    @Before
    public void setUp() {
        try {
            C8Collection c = db.collection(COLLECTION_NAME);
            c.drop();
        } catch (final C8DBException e) {
        }

        try {
            C8Collection c = db.collection(COLLECTION_NAME + "1");
            c.drop();
        } catch (final C8DBException e) {
        }

        try {
            C8Collection c = db.collection(COLLECTION_NAME + "2");
            c.drop();
        } catch (final C8DBException e) {
        }

        try {
            C8Collection c = db.collection(COLLECTION_NAME + "edge");
            c.drop();
        } catch (final C8DBException e) {
        }

        try {
            C8Collection c = db.collection(COLLECTION_NAME + "from");
            c.drop();
        } catch (final C8DBException e) {
        }

        try {
            C8Collection c = db.collection(COLLECTION_NAME + "to");
            c.drop();
        } catch (final C8DBException e) {
        }
    }

    @Test
    public void create() {
        try {
            final Boolean result = c8DB.db(C8Defaults.DEFAULT_TENANT, BaseTest.TEST_DB + "1").create();
            assertThat(result, is(true));
        } finally {
            try {
                c8DB.db(C8Defaults.DEFAULT_TENANT, BaseTest.TEST_DB + "1").drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void getVersion() {
        final C8DBVersion version = db.getVersion();
        assertThat(version, is(notNullValue()));
        assertThat(version.getServer(), is(notNullValue()));
        assertThat(version.getVersion(), is(notNullValue()));
    }

    @Test
    public void exists() {
        assertThat(db.exists(), is(true));
        assertThat(c8DB.db(C8Defaults.DEFAULT_TENANT, "no").exists(), is(false));
    }

    @Test
    public void getAccessibleDatabases() {
        final Collection<String> dbs = db.getAccessibleGeoFabrics();
        assertThat(dbs, is(notNullValue()));
        assertThat(dbs.size(), greaterThan(0));
        assertThat(dbs, hasItem("_system"));
    }

    @Test
    public void createCollection() {
        try {
            final CollectionEntity result = db.createCollection(COLLECTION_NAME, null);
            assertThat(result, is(notNullValue()));
            assertThat(result.getId(), is(notNullValue()));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void deleteCollection() {
        db.createCollection(COLLECTION_NAME, null);
        db.collection(COLLECTION_NAME).drop();
        try {
            db.collection(COLLECTION_NAME).getInfo();
            fail();
        } catch (final C8DBException e) {
        }
    }

    @Test
    public void getCollections() {
        try {
            final Collection<CollectionEntity> systemCollections = db.getCollections(null);
            db.createCollection(COLLECTION_NAME + "1", null);
            db.createCollection(COLLECTION_NAME + "2", null);
            final Collection<CollectionEntity> collections = db.getCollections(null);
            assertThat(collections.size(), is(2 + systemCollections.size()));
            assertThat(collections, is(notNullValue()));
        } finally {
            db.collection(COLLECTION_NAME + "1").drop();
            db.collection(COLLECTION_NAME + "2").drop();
        }
    }

    @Test
    public void getCollectionsExcludeSystem() {
        try {
            final CollectionsReadOptions options = new CollectionsReadOptions().excludeSystem(true);
            final Collection<CollectionEntity> nonSystemCollections = db.getCollections(options);

            assertThat(nonSystemCollections.size(), is(0));
            db.createCollection(COLLECTION_NAME + "1", null);
            db.createCollection(COLLECTION_NAME + "2", null);
            final Collection<CollectionEntity> newCollections = db.getCollections(options);
            assertThat(newCollections.size(), is(2));
            assertThat(newCollections, is(notNullValue()));
        } catch (final C8DBException e) {
            System.out.println(e.getErrorMessage());
        } finally {
            try {
                db.collection(COLLECTION_NAME + "1").drop();
                db.collection(COLLECTION_NAME + "2").drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void grantAccess() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.grantAccess("user1");
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test
    public void grantAccessRW() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.grantAccess("user1", Permissions.RW);
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test
    public void grantAccessRO() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.grantAccess("user1", Permissions.RO);
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test
    public void grantAccessNONE() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.grantAccess("user1", Permissions.NONE);
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test(expected = C8DBException.class)
    public void grantAccessUserNotFound() {
        db.grantAccess("user1", Permissions.RW);
    }

    @Test
    public void revokeAccess() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.revokeAccess("user1");
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test(expected = C8DBException.class)
    public void revokeAccessUserNotFound() {
        db.revokeAccess("user1");
    }

    @Test
    public void resetAccess() {
        try {
            c8DB.createUser("user1", "1234", null);
            db.resetAccess("user1");
        } finally {
            c8DB.deleteUser("user1");
        }
    }

    @Test(expected = C8DBException.class)
    public void resetAccessUserNotFound() {
        db.resetAccess("user1");
    }

    @Test
    public void getPermissions() {
        assertThat(Permissions.RW, is(db.getPermissions("root")));
    }

    @Test
    public void getIndex() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            final Collection<String> fields = new ArrayList<String>();
            fields.add("a");
            final IndexEntity createResult = db.collection(COLLECTION_NAME).ensureHashIndex(fields, null);
            final IndexEntity readResult = db.getIndex(createResult.getId());
            assertThat(readResult.getId(), is(createResult.getId()));
            assertThat(readResult.getType(), is(createResult.getType()));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void deleteIndex() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            final Collection<String> fields = new ArrayList<String>();
            fields.add("a");
            final IndexEntity createResult = db.collection(COLLECTION_NAME).ensureHashIndex(fields, null);
            final String id = db.deleteIndex(createResult.getId());
            assertThat(id, is(createResult.getId()));
            try {
                db.getIndex(id);
                fail();
            } catch (final C8DBException e) {
            }
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void query() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }
            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null, null, String.class);
            assertThat(cursor, is(notNullValue()));
            for (int i = 0; i < 10; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != 10));
            }
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryForEach() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }
            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null, null, String.class);
            assertThat(cursor, is(notNullValue()));
            final AtomicInteger i = new AtomicInteger(0);
            for (; cursor.hasNext(); cursor.next()) {
                i.incrementAndGet();
            }
            assertThat(i.get(), is(10));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryIterate() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }
            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null, null, String.class);
            assertThat(cursor, is(notNullValue()));
            final AtomicInteger i = new AtomicInteger(0);
            for (; cursor.hasNext(); cursor.next()) {
                i.incrementAndGet();
            }
            assertThat(i.get(), is(10));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryWithCount() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }

            final C8Cursor<String> cursor = db.query("for i in dbtest Limit 6 return i._id", null,
                    new C8qlQueryOptions().count(true), String.class);
            assertThat(cursor, is(notNullValue()));
            for (int i = 0; i < 6; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != 6));
            }
            assertThat(cursor.getCount(), is(6));

        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryWithLimit() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }

            final C8Cursor<String> cursor = db.query("for i in dbtest Limit 5 return i._id", null,
                    new C8qlQueryOptions(), String.class);
            assertThat(cursor, is(notNullValue()));
            for (int i = 0; i < 5; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != 5));
            }
            assertThat(cursor.getStats(), is(notNullValue()));
            assertThat(cursor.getStats().getFullCount(), is(10L));

        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryWithBatchSize() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }

            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null,
                    new C8qlQueryOptions().count(true), String.class);

            assertThat(cursor, is(notNullValue()));
            for (int i = 0; i < 10; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != 10));
            }
        } catch (final C8DBException e) {
            System.out.println(e.getErrorMessage());
            System.out.println(e.getErrorNum());
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryIterateWithBatchSize() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }

            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null,
                    new C8qlQueryOptions().count(true), String.class);

            assertThat(cursor, is(notNullValue()));
            final AtomicInteger i = new AtomicInteger(0);
            for (; cursor.hasNext(); cursor.next()) {
                i.incrementAndGet();
            }
            assertThat(i.get(), is(10));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryCursor() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            //batch size is 100 so we need more documents since id is returned only when batch size is exceeded
            final int numbDocs = 101;
            for (int i = 0; i < numbDocs; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }

            final int batchSize = 5;
            final C8Cursor<String> cursor = db.query("for i in dbtest return i._id", null,
                    new C8qlQueryOptions().count(true), String.class);
            assertThat(cursor, is(notNullValue()));
            assertThat(cursor.getCount(), is(numbDocs));

            final C8Cursor<String> cursor2 = db.cursor(cursor.getId(), String.class);
            assertThat(cursor2, is(notNullValue()));
            assertThat(cursor2.getCount(), is(numbDocs));
            assertThat(cursor2.hasNext(), is(true));

            for (int i = 0; i < batchSize; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != batchSize));
            }
        } finally {
            db.collection(COLLECTION_NAME).drop(false);
        }
    }

    @Test
    public void queryWithBindVars() throws InterruptedException {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 10; i++) {
                final BaseDocument baseDocument = new BaseDocument();
                baseDocument.addAttribute("age", 20 + i);
                db.collection(COLLECTION_NAME).insertDocument(baseDocument, null);
            }
            final Map<String, Object> bindVars = new HashMap<String, Object>();
            bindVars.put("@coll", COLLECTION_NAME);
            bindVars.put("age", 25);

            final C8Cursor<String> cursor = db.query("FOR t IN @@coll FILTER t.age >= @age SORT t.age RETURN t._id",
                    bindVars, null, String.class);

            assertThat(cursor, is(notNullValue()));

            for (int i = 0; i < 5; i++, cursor.next()) {
                assertThat(cursor.hasNext(), is(i != 5));
            }

        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryWithWarning() {
        final C8Cursor<String> cursor = c8DB.db().query("return 1/0", null, null, String.class);

        assertThat(cursor, is(notNullValue()));
        assertThat(cursor.getWarnings(), is(notNullValue()));
    }

    @Test
    public void queryClose() throws IOException {
        final C8Cursor<String> cursor = c8DB.db().query("for i in 1..2 return i", null,
                new C8qlQueryOptions(), String.class);
        cursor.close();
        int count = 0;
        try {
            for (; cursor.hasNext(); cursor.next(), count++) {
            }
        } catch (final C8DBException e) {
            assertThat(count, is(2));
        }
        assertThat(count, is(2));
    }

    @Test
    public void queryNoResults() throws IOException {
        try {
            db.createCollection(COLLECTION_NAME);
            final C8Cursor<BaseDocument> cursor = db.query("FOR i IN @@col RETURN i",
                    new MapBuilder().put("@col", COLLECTION_NAME).get(), null, BaseDocument.class);
            cursor.close();
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void queryWithNullBindParam() throws IOException {
        try {
            db.createCollection(COLLECTION_NAME);
            final C8Cursor<BaseDocument> cursor = db.query("FOR i IN @@col FILTER i.test == @test RETURN i",
                    new MapBuilder().put("@col", COLLECTION_NAME).put("test", null).get(), null, BaseDocument.class);
            cursor.close();
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void explainQuery() {
        final C8qlExecutionExplainEntity explain = c8DB.db().explainQuery("for i in 1..1 return i", null, null);
        assertThat(explain, is(notNullValue()));
        assertThat(explain.getPlan(), is(notNullValue()));
        assertThat(explain.getPlans(), is(nullValue()));
        final ExecutionPlan plan = explain.getPlan();
        assertThat(plan.getCollections().size(), is(0));
        assertThat(plan.getEstimatedCost(), greaterThan(0));
        assertThat(plan.getEstimatedNrItems(), greaterThan(0));
        assertThat(plan.getVariables().size(), is(2));
        assertThat(plan.getNodes().size(), is(greaterThan(0)));
    }

    @Test
    public void parseQuery() {
        final C8qlParseEntity parse = c8DB.db().parseQuery("for i in 1..1 return i");
        assertThat(parse, is(notNullValue()));
        assertThat(parse.getBindVars(), is(empty()));
        assertThat(parse.getCollections().size(), is(0));
        assertThat(parse.getAst().size(), is(1));
    }

    @Test
    @Ignore
    public void getCurrentlyRunningQueries() throws InterruptedException, ExecutionException {
        final Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                db.query("return sleep(0.2)", null, null, Void.class);
            }
        };
        t.start();
        Thread.sleep(100);
        try {
            final Collection<QueryEntity> currentlyRunningQueries = db.getCurrentlyRunningQueries();
            assertThat(currentlyRunningQueries, is(notNullValue()));
            assertThat(currentlyRunningQueries.size(), is(1));
            final QueryEntity queryEntity = currentlyRunningQueries.iterator().next();
            assertThat(queryEntity.getQuery(), is("return sleep(0.2)"));
            assertThat(queryEntity.getState(), is(QueryExecutionState.EXECUTING));
        } finally {
            t.join();
        }
    }

    @Test
    @Ignore
    public void getAndClearSlowQueries() throws InterruptedException, ExecutionException {
        final QueryTrackingPropertiesEntity properties = db.getQueryTrackingProperties();
        final Long slowQueryThreshold = properties.getSlowQueryThreshold();
        try {
            properties.setSlowQueryThreshold(1L);
            db.setQueryTrackingProperties(properties);

            db.query("return sleep(1.1)", null, null, Void.class);
            final Collection<QueryEntity> slowQueries = db.getSlowQueries();
            assertThat(slowQueries, is(notNullValue()));
            assertThat(slowQueries.size(), is(1));
            final QueryEntity queryEntity = slowQueries.iterator().next();
            assertThat(queryEntity.getQuery(), is("return sleep(1.1)"));

            db.clearSlowQueries();
            assertThat(db.getSlowQueries().size(), is(0));
        } finally {
            properties.setSlowQueryThreshold(slowQueryThreshold);
            db.setQueryTrackingProperties(properties);
        }
    }
    
    @Test
    @Ignore
    public void killQuery() throws InterruptedException, ExecutionException {
        final Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    db.query("return sleep(0.2)", null, null, Void.class);
                    fail();
                } catch (final C8DBException e) {
                }
            }
        };
        t.start();
        Thread.sleep(100);
        final Collection<QueryEntity> currentlyRunningQueries = db.getCurrentlyRunningQueries();
        assertThat(currentlyRunningQueries, is(notNullValue()));
        assertThat(currentlyRunningQueries.size(), is(1));

        final QueryEntity queryEntity = currentlyRunningQueries.iterator().next();
        db.killQuery(queryEntity.getId());
    }

    @Test
    public void createGraph() {
        try {
            final GraphEntity result = db.createGraph(GRAPH_NAME, null, null);
            assertThat(result, is(notNullValue()));
            assertThat(result.getName(), is(GRAPH_NAME));
        } finally {
            db.graph(GRAPH_NAME).drop();
        }
    }

    @Test
    public void getGraphs() {
        try {
            db.createGraph(GRAPH_NAME, null, null);
            final Collection<GraphEntity> graphs = db.getGraphs();
            assertThat(graphs, is(notNullValue()));
            assertThat(graphs.size(), is(1));
            assertThat(graphs.iterator().next().getName(), is(GRAPH_NAME));
        } finally {
            db.graph(GRAPH_NAME).drop();
        }
    }

    @Test
    public void transactionString() {
        final C8TransactionOptions options = new C8TransactionOptions().params("test");
        final String result = db.transaction("function (params) {return params;}", String.class, options);
        assertThat(result, is("test"));
    }

    @Test
    public void transactionNumber() {
        final C8TransactionOptions options = new C8TransactionOptions().params(5);
        final Integer result = db.transaction("function (params) {return params;}", Integer.class, options);
        assertThat(result, is(5));
    }

    @Test
    public void transactionVPack() throws VPackException {
        final C8TransactionOptions options = new C8TransactionOptions().params(new VPackBuilder().add("test").slice());
        final VPackSlice result = db.transaction("function (params) {return params;}", VPackSlice.class, options);
        assertThat(result.isString(), is(true));
        assertThat(result.getAsString(), is("test"));
    }

    @Test
    public void transactionVPackObject() throws VPackException {
        final VPackSlice params = new VPackBuilder().add(ValueType.OBJECT).add("foo", "hello").add("bar", "world")
                .close().slice();
        final C8TransactionOptions options = new C8TransactionOptions().params(params);
        final String result = db.transaction("function (params) { return params['foo'] + ' ' + params['bar'];}",
                String.class, options);
        assertThat(result, is("hello world"));
    }

    @Test
    public void transactionVPackArray() throws VPackException {
        final VPackSlice params = new VPackBuilder().add(ValueType.ARRAY).add("hello").add("world").close().slice();
        final C8TransactionOptions options = new C8TransactionOptions().params(params);
        final String result = db.transaction("function (params) { return params[0] + ' ' + params[1];}", String.class,
                options);
        assertThat(result, is("hello world"));
    }

    @Test
    public void transactionMap() {
        final Map<String, Object> params = new MapBuilder().put("foo", "hello").put("bar", "world").get();
        final C8TransactionOptions options = new C8TransactionOptions().params(params);
        final String result = db.transaction("function (params) { return params['foo'] + ' ' + params['bar'];}",
                String.class, options);
        assertThat(result, is("hello world"));
    }

    @Test
    public void transactionArray() {
        final String[] params = new String[] { "hello", "world" };
        final C8TransactionOptions options = new C8TransactionOptions().params(params);
        final String result = db.transaction("function (params) { return params[0] + ' ' + params[1];}", String.class,
                options);
        assertThat(result, is("hello world"));
    }

    @Test
    public void transactionCollection() {
        final Collection<String> params = new ArrayList<String>();
        params.add("hello");
        params.add("world");
        final C8TransactionOptions options = new C8TransactionOptions().params(params);
        final String result = db.transaction("function (params) { return params[0] + ' ' + params[1];}", String.class,
                options);
        assertThat(result, is("hello world"));
    }

    @Test
    public void transactionInsertJson() {
        try {
            db.createCollection(COLLECTION_NAME);
            final C8TransactionOptions options = new C8TransactionOptions().params("{\"_key\":\"0\"}")
                    .writeCollections(COLLECTION_NAME);
            // @formatter:off
            db.transaction("function (params) { " + "var db = require('internal').db;" + "db." + COLLECTION_NAME
                    + ".save(JSON.parse(params));" + "}", Void.class, options);
            // @formatter:on
            assertThat(db.collection(COLLECTION_NAME).count().getCount(), is(1L));
            assertThat(db.collection(COLLECTION_NAME).getDocument("0", String.class), is(notNullValue()));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void transactionExclusiveWrite() {
        if (!requireVersion(3, 4)) {
            return;
        }
        try {
            db.createCollection(COLLECTION_NAME);
            final C8TransactionOptions options = new C8TransactionOptions().params("{\"_key\":\"0\"}")
                    .exclusiveCollections(COLLECTION_NAME);
            // @formatter:off
            db.transaction("function (params) { " + "var db = require('internal').db;" + "db." + COLLECTION_NAME
                    + ".save(JSON.parse(params));" + "}", Void.class, options);
            // @formatter:on
            assertThat(db.collection(COLLECTION_NAME).count().getCount(), is(1L));
            assertThat(db.collection(COLLECTION_NAME).getDocument("0", String.class), is(notNullValue()));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void transactionEmpty() {
        db.transaction("function () {}", null, null);
    }

    @Test
    public void transactionallowImplicit() {
        try {
            db.createCollection("someCollection", null);
            db.createCollection("someOtherCollection", null);
            final String action = "function (params) {" + "var db = require('internal').db;"
                    + "return {'a':db.someCollection.all().toArray()[0], 'b':db.someOtherCollection.all().toArray()[0]};"
                    + "}";
            final C8TransactionOptions options = new C8TransactionOptions().readCollections("someCollection");
            db.transaction(action, VPackSlice.class, options);
            try {
                options.allowImplicit(false);
                db.transaction(action, VPackSlice.class, options);
                fail();
            } catch (final C8DBException e) {
            }
        } finally {
            db.collection("someCollection").drop();
            db.collection("someOtherCollection").drop();
        }
    }

    protected static class TransactionTestEntity {
        private String value;

        public TransactionTestEntity() {
            super();
        }
    }

    @Test
    public void transactionPojoReturn() {
        final String action = "function() { return {'value':'hello world'}; }";
        final TransactionTestEntity res = db.transaction(action, TransactionTestEntity.class, new C8TransactionOptions());
        assertThat(res, is(notNullValue()));
        assertThat(res.value, is("hello world"));
    }

    @Test
    public void getInfo() {
        final DatabaseEntity info = db.getInfo();
        assertThat(info, is(notNullValue()));
        assertThat(info.getId(), is(notNullValue()));
        assertThat(info.getName(), is(TEST_DB));
        assertThat(info.getPath(), is(notNullValue()));
        assertThat(info.getIsSystem(), is(false));
    }

    @Test
    public void executeTraversal() {
        try {
            db.createCollection("person", null);
            db.createCollection("knows", new CollectionCreateOptions().type(CollectionType.EDGES));
            for (final String e : new String[] { "Alice", "Bob", "Charlie", "Dave", "Eve" }) {
                final BaseDocument doc = new BaseDocument();
                doc.setKey(e);
                db.collection("person").insertDocument(doc, null);
            }
            for (final String[] e : new String[][] { new String[] { "Alice", "Bob" }, new String[] { "Bob", "Charlie" },
                    new String[] { "Bob", "Dave" }, new String[] { "Eve", "Alice" }, new String[] { "Eve", "Bob" } }) {
                final BaseEdgeDocument edge = new BaseEdgeDocument();
                edge.setKey(e[0] + "_knows_" + e[1]);
                edge.setFrom("person/" + e[0]);
                edge.setTo("person/" + e[1]);
                db.collection("knows").insertDocument(edge, null);
            }
            final TraversalOptions options = new TraversalOptions().edgeCollection("knows").startVertex("person/Alice")
                    .direction(Direction.outbound);
            final TraversalEntity<BaseDocument, BaseEdgeDocument> traversal = db.executeTraversal(BaseDocument.class,
                    BaseEdgeDocument.class, options);

            assertThat(traversal, is(notNullValue()));

            final Collection<BaseDocument> vertices = traversal.getVertices();
            assertThat(vertices, is(notNullValue()));
            assertThat(vertices.size(), is(4));

            final Iterator<BaseDocument> verticesIterator = vertices.iterator();
            final Collection<String> v = Arrays.asList(new String[] { "Alice", "Bob", "Charlie", "Dave" });
            for (; verticesIterator.hasNext();) {
                assertThat(v.contains(verticesIterator.next().getKey()), is(true));
            }

            final Collection<PathEntity<BaseDocument, BaseEdgeDocument>> paths = traversal.getPaths();
            assertThat(paths, is(notNullValue()));
            assertThat(paths.size(), is(4));

            assertThat(paths.iterator().hasNext(), is(true));
            final PathEntity<BaseDocument, BaseEdgeDocument> first = paths.iterator().next();
            assertThat(first, is(notNullValue()));
            assertThat(first.getEdges().size(), is(0));
            assertThat(first.getVertices().size(), is(1));
            assertThat(first.getVertices().iterator().next().getKey(), is("Alice"));
        } finally {
            db.collection("person").drop();
            db.collection("knows").drop();
        }
    }

    @Test
    public void getDocument() {
        try {
            db.createCollection(COLLECTION_NAME);
            final BaseDocument value = new BaseDocument();
            value.setKey("123");
            db.collection(COLLECTION_NAME).insertDocument(value);
            final BaseDocument document = db.getDocument(COLLECTION_NAME + "/123", BaseDocument.class);
            assertThat(document, is(notNullValue()));
            assertThat(document.getKey(), is("123"));
        } finally {
            db.collection(COLLECTION_NAME).drop();
        }
    }

    @Test
    public void shouldIncludeExceptionMessage() {
        if (!requireVersion(3, 2)) {
            final String exceptionMessage = "My error context";
            final String action = "function (params) {" + "throw '" + exceptionMessage + "';" + "}";
            try {
                db.transaction(action, VPackSlice.class, null);
                fail();
            } catch (final C8DBException e) {
                assertTrue(e.getException().contains(exceptionMessage));
            }
        }
    }

    @Test(expected = C8DBException.class)
    public void getDocumentWrongId() {
        db.getDocument("123", BaseDocument.class);
    }

    @Test
    public void createGetStream() {
        int before = db.getPersistentStreams(null).size();
        db.createPersistentStream(STREAM_NAME, null);
        final Collection<C8StreamEntity> streams = db.getPersistentStreams(null);

        assertThat(streams, is(notNullValue()));
        assertThat(streams.size() - before, is(1));
    }
    
    @Test
    public void userQuery() {
        try {
            db.createCollection(COLLECTION_NAME, null);
            for (int i = 0; i < 2; i++) {
                db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(), null);
            }
            UserQueryEntity userQuery = db.createUserQuery(new UserQueryOptions().name("test").value("for i in dbtest return i"));
            assertThat(userQuery, is(notNullValue()));
            assertThat(userQuery.getName(), is ("test"));
            db.restql().getUserQueries();
            db.restql().getUserQueries("test");
        } finally {
            db.restql().drop("test");
            db.collection(COLLECTION_NAME).drop();
        }
    }
}
