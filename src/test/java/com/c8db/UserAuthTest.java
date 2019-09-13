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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.c8db.entity.BaseDocument;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.Permissions;
import com.c8db.internal.C8Defaults;
import com.c8db.model.C8qlQueryOptions;
import com.c8db.model.CollectionPropertiesOptions;
import com.c8db.model.HashIndexOptions;
import com.c8db.model.UserUpdateOptions;
import com.c8db.util.MapBuilder;

/**
 *
 */
@RunWith(Parameterized.class)
@Ignore
public class UserAuthTest {

    private static final String DB_NAME = "AuthUnitTestDB";
    private static final String DB_NAME_NEW = DB_NAME + "new";
    private static final String COLLECTION_NAME = "AuthUnitTestCollection";
    private static final String COLLECTION_NAME_NEW = COLLECTION_NAME + "new";
    private static final String USER_NAME = "AuthUnitTestUser";
    private static final String USER_NAME_NEW = USER_NAME + "new";

    public static class UserAuthParam {
        Protocol protocol;
        Permissions systemPermission;
        Permissions dbPermission;
        Permissions colPermission;

        public UserAuthParam(final Protocol protocol, final Permissions systemPermission,
                final Permissions dbPermission, final Permissions colPermission) {
            super();
            this.protocol = protocol;
            this.systemPermission = systemPermission;
            this.dbPermission = dbPermission;
            this.colPermission = colPermission;
        }

    }

    @Parameters
    public static Collection<UserAuthParam> params() {
        final Collection<UserAuthParam> params = new ArrayList<UserAuthParam>();
        final Permissions[] permissions = new Permissions[] { Permissions.RW, Permissions.RO, Permissions.NONE };
        for (final Protocol protocol : new Protocol[] { Protocol.VST, Protocol.HTTP_JSON, Protocol.HTTP_VPACK }) {
            for (final Permissions systemPermission : permissions) {
                for (final Permissions dbPermission : permissions) {
                    for (final Permissions colPermission : permissions) {
                        params.add(new UserAuthParam(protocol, systemPermission, dbPermission, colPermission));
                    }
                }
            }
        }
        return params;
    }

    private static C8DB arangoDB;
    private static C8DB arangoDBRoot;
    private final UserAuthParam param;
    private final String details;

    public UserAuthTest(final UserAuthParam param) {
        super();
        this.param = param;
        if (arangoDB != null || arangoDBRoot != null) {
            shutdown();
        }
        arangoDBRoot = new C8DB.Builder().useProtocol(param.protocol).build();
        arangoDBRoot.createUser(USER_NAME, "");
        arangoDB = new C8DB.Builder().useProtocol(param.protocol).user(USER_NAME).build();
        arangoDBRoot.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME, "", "tonchev-europe-west4");
        arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME);
        arangoDBRoot.db().grantAccess(USER_NAME, param.systemPermission);
        arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).grantAccess(USER_NAME, param.dbPermission);
        arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).grantAccess(USER_NAME,
                param.colPermission);
        details = new StringBuffer().append(param.protocol).append("_").append(param.systemPermission).append("_")
                .append(param.dbPermission).append("_").append(param.colPermission).toString();
    }

    @AfterClass
    public static void shutdown() {
        arangoDBRoot.deleteUser(USER_NAME);
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop();
        } catch (final C8DBException e) {
        }
        if (arangoDB != null) {
            arangoDB.shutdown();
        }
        arangoDBRoot.shutdown();
        arangoDB = null;
        arangoDBRoot = null;
    }

    @Test
    public void createDatabase() {
        try {
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    assertThat(details, arangoDB.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "",
                            "tonchev-europe-west4"), is(true));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.getGeoFabrics(), hasItem(DB_NAME_NEW));
            } else {
                try {
                    arangoDB.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "", "tonchev-europe-west4");
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.getGeoFabrics(), not(hasItem(DB_NAME_NEW)));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void dropDatabase() {
        try {
            arangoDBRoot.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "", "tonchev-europe-west4");
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    assertThat(details, arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop(), is(true));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.getGeoFabrics(), not(hasItem(DB_NAME)));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.getGeoFabrics(), hasItem(DB_NAME));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void createUser() {
        try {
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.createUser(USER_NAME_NEW, "");
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.getUsers(), is(notNullValue()));
            } else {
                try {
                    arangoDB.createUser(USER_NAME_NEW, "");
                    fail(details);
                } catch (final C8DBException e) {
                }
                try {
                    arangoDBRoot.getUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteUser() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.deleteUser(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
                try {
                    arangoDBRoot.getUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            } else {
                try {
                    arangoDB.deleteUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.getUsers(), is(notNullValue()));
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUser() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.updateUser(USER_NAME_NEW, new UserUpdateOptions().active(false));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.getUser(USER_NAME_NEW).getActive(), is(false));
            } else {
                try {
                    arangoDB.updateUser(USER_NAME_NEW, new UserUpdateOptions().active(false));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.getUser(USER_NAME_NEW).getActive(), is(true));
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void grantUserDBAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.db().grantAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.db().grantAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void resetUserDBAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            arangoDBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).resetAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).resetAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void grantUserCollcetionAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .grantAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .grantAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void resetUserCollectionAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            arangoDBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .resetAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .resetAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUserDefaultDatabaseAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            arangoDBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.grantDefaultDatabaseAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.grantDefaultDatabaseAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUserDefaultCollectionAccess() {
        try {
            arangoDBRoot.createUser(USER_NAME_NEW, "");
            arangoDBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    arangoDB.grantDefaultCollectionAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    arangoDB.grantDefaultCollectionAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void createCollection() {
        try {
            if (Permissions.RW.equals(param.dbPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details,
                        arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo(),
                        is(notNullValue()));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
                try {
                    arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo();
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void dropCollection() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).grantAccess(USER_NAME,
                    param.colPermission);
            if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
                } catch (final C8DBException e) {
                    fail(details);
                }
                try {
                    arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo();
                    fail(details);
                } catch (final C8DBException e) {
                }
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details,
                        arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo(),
                        is(notNullValue()));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void seeCollection() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                final Collection<CollectionEntity> collections = arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                        .getCollections();
                boolean found = false;
                for (final CollectionEntity collection : collections) {
                    if (collection.getName().equals(COLLECTION_NAME)) {
                        found = true;
                        break;
                    }
                }
                assertThat(details, found, is(true));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else if (Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission)) {
            final Collection<CollectionEntity> collections = arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                    .getCollections();
            boolean found = false;
            for (final CollectionEntity collection : collections) {
                if (collection.getName().equals(COLLECTION_NAME)) {
                    found = true;
                    break;
                }
            }
            assertThat(details, found, is(false));
        }
    }

    @Test
    public void readCollectionInfo() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                assertThat(details,
                        arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getInfo(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getInfo();
                fail(details);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void readCollectionProperties() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                assertThat(details,
                        arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getProperties(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getProperties();
                fail(details);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void writeCollectionProperties() {
        if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
            try {
                assertThat(
                        details, arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                                .changeProperties(new CollectionPropertiesOptions().waitForSync(true)),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
            assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .getProperties().getWaitForSync(), is(true));
        } else {
            try {
                arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .changeProperties(new CollectionPropertiesOptions().waitForSync(true));
                fail(details);
            } catch (final C8DBException e) {
            }
            assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .getProperties().getWaitForSync(), is(false));
        }
    }

    @Test
    public void readCollectionIndexes() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                assertThat(details,
                        arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getIndexes(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getIndexes();
                fail(details);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void createCollectionIndex() {
        String id = null;
        try {
            if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
                try {
                    final IndexEntity createHashIndex = arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                            .collection(COLLECTION_NAME).ensureHashIndex(Arrays.asList("a"), new HashIndexOptions());
                    assertThat(details, createHashIndex, is(notNullValue()));
                    id = createHashIndex.getId();
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(2));
            } else {
                try {
                    final IndexEntity createHashIndex = arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                            .collection(COLLECTION_NAME).ensureHashIndex(Arrays.asList("a"), new HashIndexOptions());
                    id = createHashIndex.getId();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(1));
            }
        } finally {
            if (id != null) {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
            }
        }
    }

    @Test
    public void dropCollectionIndex() {
        final String id = arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                .ensureHashIndex(Arrays.asList("a"), new HashIndexOptions()).getId();
        try {
            if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(1));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(2));
            }
        } finally {
            if (id != null) {
                try {
                    arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                } catch (final C8DBException e) {
                }
            }
        }
    }

    @Test
    public void truncateCollection() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void readDocumentByKey() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
                assertThat(details, arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(notNullValue()));
            } else {
                assertThat(details, arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(nullValue()));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void readDocumentByAql() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
                assertThat(details,
                        arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                                .query("FOR i IN @@col RETURN i", new MapBuilder().put("@col", COLLECTION_NAME).get(),
                                        new C8qlQueryOptions(), BaseDocument.class)
                                .asListRemaining().size(),
                        is(1));
            } else {
                assertThat(details, arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(nullValue()));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void insertDocument() {
        try {
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .insertDocument(new BaseDocument("123"));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .insertDocument(new BaseDocument("123"));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateDocumentByKey() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).updateDocument("123",
                            new BaseDocument(new MapBuilder().put("test", "test").get()));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test").toString(), is("test"));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).updateDocument("123",
                            new BaseDocument(new MapBuilder().put("test", "test").get()));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test"), is(nullValue()));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateDocumentByAql() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query(
                            "FOR i IN @@col UPDATE i WITH @newDoc IN @@col",
                            new MapBuilder().put("@col", COLLECTION_NAME)
                                    .put("newDoc", new BaseDocument(new MapBuilder().put("test", "test").get())).get(),
                            new C8qlQueryOptions(), Void.class);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test").toString(), is("test"));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query(
                            "FOR i IN @@col UPDATE i WITH @newDoc IN @@col",
                            new MapBuilder().put("@col", COLLECTION_NAME)
                                    .put("newDoc", new BaseDocument(new MapBuilder().put("test", "test").get())).get(),
                            new C8qlQueryOptions(), Void.class);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test"), is(nullValue()));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteDocumentByKey() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteDocumentByAql() {
        try {
            arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query("REMOVE @key IN @@col",
                            new MapBuilder().put("key", "123").put("@col", COLLECTION_NAME).get(),
                            new C8qlQueryOptions(), Void.class);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    arangoDB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query("REMOVE @key IN @@col",
                            new MapBuilder().put("key", "123").put("@col", COLLECTION_NAME).get(),
                            new C8qlQueryOptions(), Void.class);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                arangoDBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

}
