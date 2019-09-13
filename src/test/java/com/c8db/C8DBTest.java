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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.c8db.C8DB.Builder;
import com.c8db.entity.C8DBVersion;
import com.c8db.entity.DataCenterEntity;
import com.c8db.entity.DcInfoEntity;
import com.c8db.entity.GeoFabricEntity;
import com.c8db.entity.Permissions;
import com.c8db.entity.UserEntity;
import com.c8db.internal.C8Defaults;
import com.c8db.internal.C8RequestParam;
import com.c8db.model.UserCreateOptions;
import com.c8db.model.UserUpdateOptions;

/**
 * 
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8DBTest {

    @Parameters
    public static Collection<C8DB.Builder> builders() {
        return Arrays.asList(//
//				new ArangoDB.Builder().useProtocol(Protocol.VST), //
                new C8DB.Builder().useProtocol(Protocol.HTTP_JSON) //
//				new ArangoDB.Builder().useProtocol(Protocol.HTTP_VPACK) //
        );
    }

    private static final String ROOT = "root";
    private static final String USER = "test_user";
    private static final String PW = "machts der hund";
    private final C8DB c8DB;

    public C8DBTest(final Builder builder) {
        super();
        c8DB = builder.build();
    }

    @Test
    public void getVersion() {
        final C8DBVersion version = c8DB.getVersion();
        assertThat(version, is(notNullValue()));
        assertThat(version.getServer(), is(notNullValue()));
        assertThat(version.getVersion(), is(notNullValue()));
    }

    @Test
    public void createAndDeleteDatabase() {
        final String dbName = "testDB-" + UUID.randomUUID().toString();
        final Boolean resultCreate = c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, "",
                C8Defaults.DEFAULT_DC_LIST);
        assertThat(resultCreate, is(true));
        System.out.println(c8DB.getGeoFabrics());
        final Boolean resultDelete = c8DB.db(C8Defaults.DEFAULT_TENANT, dbName).drop();
        assertThat(resultDelete, is(true));
    }

    @Test
    public void getDatabases() {
        final String dbName = "testDB-" + UUID.randomUUID().toString();
        Collection<String> dbs = c8DB.getGeoFabrics();
        assertThat(dbs, is(notNullValue()));
        assertThat(dbs.size(), is(greaterThan(0)));
        final int dbCount = dbs.size();
        assertThat(dbs.contains("_system"), is(true));
        c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, "", C8Defaults.DEFAULT_DC_LIST);
        dbs = c8DB.getGeoFabrics();
        assertThat(dbs.size(), is(greaterThan(dbCount)));
        assertThat(dbs, hasItem("_system"));
        assertThat(dbs, hasItem(dbName));
        c8DB.db(C8Defaults.DEFAULT_TENANT, dbName, "", "").drop();
    }

    @Test
    public void getAccessibleDatabases() {
        final Collection<String> dbs = c8DB.getAccessibleGeoFabrics();
        assertThat(dbs, is(notNullValue()));
        assertThat(dbs.size(), greaterThan(0));
        assertThat(dbs, hasItem("_system"));
    }

    @Test
    public void getAccessibleDatabasesFor() {
        final Collection<String> dbs = c8DB.getAccessibleGeoFabricsFor("root");
        assertThat(dbs, is(notNullValue()));
        assertThat(dbs.size(), greaterThan(0));
        assertThat(dbs, hasItem("_system"));
    }

    @Test
    public void updateDCList() {
        final String dbName = "testDB-" + UUID.randomUUID().toString();
        c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, "",
                C8Defaults.DEFAULT_DC_LIST.split(",")[0]);
        Boolean result = c8DB.updateDataCentersForGeoFabric(C8Defaults.DEFAULT_TENANT, dbName,
                C8Defaults.DEFAULT_DC_LIST);
        assertThat(result, is(true));
        c8DB.db(C8Defaults.DEFAULT_TENANT, dbName).drop();
    }

    @Test
    public void getInfo() {
        final String dbName = "testDB-" + UUID.randomUUID().toString();
        try {
            c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, "", C8Defaults.DEFAULT_DC_LIST);
            final GeoFabricEntity dbInfo = c8DB.getGeoFabricInformation(C8Defaults.DEFAULT_TENANT, dbName);
            assertThat(dbInfo.getName(), is(dbName));
            assertThat(dbInfo.getIsSystem(), is(false));
            assertThat(dbInfo.getOptions(), is(notNullValue()));
            assertThat(dbInfo.getOptions().getDcList(), is(C8Defaults.DEFAULT_DC_LIST));
        } finally {
            c8DB.db(C8Defaults.DEFAULT_TENANT, dbName).drop();
        }
    }
    
    @Ignore
    @Test
    public void updateDcList() {
        final String dbName = "testDB-" + UUID.randomUUID().toString();
        try {
            c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, "", C8Defaults.DEFAULT_DC_LIST);
            String spotDC = C8Defaults.DEFAULT_DC_LIST.split(",")[0];
            Boolean asdf = c8DB.updateSpotRegionForGeoFabric(C8Defaults.DEFAULT_TENANT, dbName, spotDC);
            System.out.println(asdf);
            final GeoFabricEntity dbInfo = c8DB.getGeoFabricInformation(C8Defaults.DEFAULT_TENANT, dbName);
            assertThat(dbInfo.getName(), is(dbName));
            assertThat(dbInfo.getOptions(), is(notNullValue()));
            assertThat(dbInfo.getOptions().getSpotDc(), is(spotDC));
        } finally {
            c8DB.db(C8Defaults.DEFAULT_TENANT, dbName).drop();
        }
    }

    @Test
    public void getEdgeLocations() {
        List<DataCenterEntity> dcs = c8DB.getEdgeLocations(C8Defaults.DEFAULT_TENANT);
        assertThat(dcs, is(notNullValue()));
        assertThat(dcs.size(), is(1));
        DataCenterEntity dc = dcs.get(0);
        assertThat(dc.getTenant(), is(C8Defaults.DEFAULT_TENANT));
        assertThat(dc.getDcInfo(), is(notNullValue()));
        assertThat(dc.getDcInfo().size(), is(C8Defaults.DEFAULT_DC_LIST.split(",").length));
    }
    
    @Test
    public void getAllEdgeLocations() {
        List<DcInfoEntity> dcs = c8DB.getAllEdgeLocations();
        assertThat(dcs, is(notNullValue()));
        assertThat(dcs.size(), is(C8Defaults.DEFAULT_DC_LIST.split(",").length));
    }
 
    @Test
    public void getLocalEdgeLocation() {
        DcInfoEntity dc = c8DB.getLocalEdgeLocation();
        assertThat(dc, is(notNullValue()));
        dc = c8DB.getEdgeLocation(C8Defaults.DEFAULT_DC_LIST.split(",")[0]);
        assertThat(dc, is(notNullValue()));
        assertThat(dc.getName(), is(C8Defaults.DEFAULT_DC_LIST.split(",")[0]));
    }

    @Test
    public void createAndDeleteUser() {
        try {
            final UserEntity result = c8DB.createUser(USER, PW, null);
            assertThat(result, is(notNullValue()));
            assertThat(result.getUser(), is(USER));
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void getUserRoot() {
        final UserEntity user = c8DB.getUser(ROOT);
        assertThat(user, is(notNullValue()));
        assertThat(user.getUser(), is(ROOT));
    }

    @Test
    public void getUser() {
        try {
            c8DB.createUser(USER, PW, null);
            final UserEntity user = c8DB.getUser(USER);
            assertThat(user.getUser(), is(USER));
        } finally {
            c8DB.deleteUser(USER);
        }

    }

    @Test
    public void getUsersOnlyRoot() {
        final Collection<UserEntity> users = c8DB.getUsers();
        assertThat(users, is(notNullValue()));
        assertThat(users.size(), greaterThan(0));
    }

    @Test
    public void getUsers() {
        try {
            // Allow & account for pre-existing users other than ROOT:
            final Collection<UserEntity> initialUsers = c8DB.getUsers();

            c8DB.createUser(USER, PW, null);
            final Collection<UserEntity> users = c8DB.getUsers();
            assertThat(users, is(notNullValue()));
            assertThat(users.size(), is(initialUsers.size() + 1));

            final List<Matcher<? super String>> matchers = new ArrayList<Matcher<? super String>>(users.size());
            // Add initial users, including root:
            for (final UserEntity userEntity : initialUsers) {
                matchers.add(is(userEntity.getUser()));
            }
            // Add USER:
            matchers.add(is(C8Defaults.DEFAULT_TENANT + "." + USER));

            for (final UserEntity user : users) {
                assertThat(user.getUser(), anyOf(matchers));
            }
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void updateUserNoOptions() {
        try {
            c8DB.createUser(USER, PW, null);
            c8DB.updateUser(USER, null);
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void updateUser() {
        try {
            final Map<String, Object> extra = new HashMap<String, Object>();
            extra.put("hund", false);
            c8DB.createUser(USER, PW, new UserCreateOptions().extra(extra));
            extra.put("hund", true);
            extra.put("mund", true);
            final UserEntity user = c8DB.updateUser(USER, new UserUpdateOptions().extra(extra));
            assertThat(user, is(notNullValue()));
            assertThat(user.getExtra().size(), is(2));
            assertThat(user.getExtra().get("hund"), is(notNullValue()));
            assertThat(Boolean.valueOf(String.valueOf(user.getExtra().get("hund"))), is(true));
            final UserEntity user2 = c8DB.getUser(USER);
            assertThat(user2.getExtra().size(), is(2));
            assertThat(user2.getExtra().get("hund"), is(notNullValue()));
            assertThat(Boolean.valueOf(String.valueOf(user2.getExtra().get("hund"))), is(true));
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Ignore("issue 706")
    @Test
    public void replaceUser() {
        try {
            final Map<String, Object> extra = new HashMap<String, Object>();
            extra.put("hund", false);
            c8DB.createUser(USER, PW, new UserCreateOptions().extra(extra));
            extra.remove("hund");
            extra.put("mund", true);
            final UserEntity user = c8DB.replaceUser(USER, new UserUpdateOptions().extra(extra));
            assertThat(user, is(notNullValue()));
            assertThat(user.getExtra().size(), is(1));
            assertThat(user.getExtra().get("mund"), is(notNullValue()));
            assertThat(Boolean.valueOf(String.valueOf(user.getExtra().get("mund"))), is(true));
            final UserEntity user2 = c8DB.getUser(USER);
            assertThat(user2.getExtra().size(), is(1));
            assertThat(user2.getExtra().get("mund"), is(notNullValue()));
            assertThat(Boolean.valueOf(String.valueOf(user2.getExtra().get("mund"))), is(true));
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void updateUserDefaultDatabaseAccess() {
        try {
            c8DB.createUser(USER, PW);
            c8DB.grantDefaultDatabaseAccess(USER, Permissions.RW);
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void updateUserDefaultCollectionAccess() {
        try {
            c8DB.createUser(USER, PW);
            c8DB.grantDefaultCollectionAccess(USER, Permissions.RW);
        } finally {
            c8DB.deleteUser(USER);
        }
    }

    @Test
    public void authenticationFailPassword() {
        final C8DB arangoDB = new C8DB.Builder().password("no").build();
        try {
            arangoDB.getVersion();
            fail();
        } catch (final C8DBException e) {

        }
    }

    @Test
    public void authenticationFailUser() {
        final C8DB arangoDB = new C8DB.Builder().user("no").build();
        try {
            arangoDB.getVersion();
            fail();
        } catch (final C8DBException e) {

        }
    }
    @Test
    public void c8DBException() {
        try {
            c8DB.db(C8RequestParam.DEMO_TENANT, "no").getInfo();
            fail();
        } catch (final C8DBException e) {
            assertThat(e.getResponseCode(), is(404));
            assertThat(e.getErrorNum(), is(1228));
            assertThat(e.getErrorMessage(), is("database not found"));
        }
    }

    @Test
    public void fallbackHost() {
        final C8DB arangoDB = new C8DB.Builder().host("not-accessible", 8529).host("127.0.0.1", 8529).build();
        final C8DBVersion version = arangoDB.getVersion();
        assertThat(version, is(notNullValue()));
    }

    @Test(expected = C8DBException.class)
    public void loadproperties() {
        new C8DB.Builder().loadProperties(C8DBTest.class.getResourceAsStream("/c8db-bad.properties"));
    }

    @Test(expected = C8DBException.class)
    public void loadproperties2() {
        new C8DB.Builder().loadProperties(C8DBTest.class.getResourceAsStream("/c8db-bad2.properties"));
    }

    @Test
    public void accessMultipleDatabases() {
        String db1 = "multipledb1";
        String db2 = "multipledb2";

        try {
            c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, db1, "", C8Defaults.DEFAULT_DC_LIST);
            c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, db2, "", C8Defaults.DEFAULT_DC_LIST);

            final C8DBVersion version1 = c8DB.db(C8Defaults.DEFAULT_TENANT, db1).getVersion();
            assertThat(version1, is(notNullValue()));
            final C8DBVersion version2 = c8DB.db(C8Defaults.DEFAULT_TENANT, db2).getVersion();
            assertThat(version2, is(notNullValue()));
        } finally {
            c8DB.db(C8Defaults.DEFAULT_TENANT, db1).drop();
            c8DB.db(C8Defaults.DEFAULT_TENANT, db2).drop();
        }
    }
}
