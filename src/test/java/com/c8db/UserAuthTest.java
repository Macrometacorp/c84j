/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
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

import com.c8db.internal.C8RequestParam;
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
    private static final String EMAIL = "aa@cc.bb";
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

    private static C8DB c8DB;
    private static C8DB c8DBRoot;
    private final UserAuthParam param;
    private final String details;

    public UserAuthTest(final UserAuthParam param) {
        super();
        this.param = param;
        if (c8DB != null || c8DBRoot != null) {
            shutdown();
        }
        c8DBRoot = new C8DB.Builder().useProtocol(param.protocol).build();
        c8DBRoot.createUser(USER_NAME, "", EMAIL);
        c8DB = new C8DB.Builder().useProtocol(param.protocol).user(USER_NAME).build();
        c8DBRoot.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME, "", C8Defaults.DEFAULT_DC_LIST, DB_NAME);
        c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME);
        c8DBRoot.db().grantAccess(USER_NAME, param.systemPermission);
        c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).grantAccess(USER_NAME, param.dbPermission);
        c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).grantAccess(USER_NAME,
                param.colPermission);
        details = new StringBuffer().append(param.protocol).append("_").append(param.systemPermission).append("_")
                .append(param.dbPermission).append("_").append(param.colPermission).toString();
    }

    @AfterClass
    public static void shutdown() {
        c8DBRoot.deleteUser(USER_NAME);
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop();
        } catch (final C8DBException e) {
        }
        if (c8DB != null) {
            c8DB.shutdown();
        }
        c8DBRoot.shutdown();
        c8DB = null;
        c8DBRoot = null;
    }

    @Test
    public void createDatabase() {
        try {
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    assertThat(details, c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "",
                            C8Defaults.DEFAULT_DC_LIST, DB_NAME_NEW), is(true));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.getGeoFabrics(), hasItem(DB_NAME_NEW));
            } else {
                try {
                    c8DB.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "", C8Defaults.DEFAULT_DC_LIST, DB_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.getGeoFabrics(), not(hasItem(DB_NAME_NEW)));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void dropDatabase() {
        try {
            c8DBRoot.createGeoFabric(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW, "", C8Defaults.DEFAULT_DC_LIST, DB_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    assertThat(details, c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop(), is(true));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.getGeoFabrics(), not(hasItem(DB_NAME)));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).drop();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.getGeoFabrics(), hasItem(DB_NAME));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void createUser() {
        try {
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.createUser(USER_NAME_NEW, "", EMAIL);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.getUsers(), is(notNullValue()));
            } else {
                try {
                    c8DB.createUser(USER_NAME_NEW, "", EMAIL);
                    fail(details);
                } catch (final C8DBException e) {
                }
                try {
                    c8DBRoot.getUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteUser() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.deleteUser(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
                try {
                    c8DBRoot.getUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            } else {
                try {
                    c8DB.deleteUser(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.getUsers(), is(notNullValue()));
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUser() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.updateUser(USER_NAME_NEW, new UserUpdateOptions().active(false));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.getUser(USER_NAME_NEW).getActive(), is(false));
            } else {
                try {
                    c8DB.updateUser(USER_NAME_NEW, new UserUpdateOptions().active(false));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.getUser(USER_NAME_NEW).getActive(), is(true));
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void grantUserDBAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.db().grantAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.db().grantAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void resetUserDBAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            c8DBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).resetAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).resetAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void grantUserCollcetionAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .grantAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .grantAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void resetUserCollectionAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            c8DBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .resetAccess(USER_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .resetAccess(USER_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUserDefaultDatabaseAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            c8DBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.grantDefaultDatabaseAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.grantDefaultDatabaseAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateUserDefaultCollectionAccess() {
        try {
            c8DBRoot.createUser(USER_NAME_NEW, "", EMAIL);
            c8DBRoot.db().grantAccess(USER_NAME_NEW);
            if (Permissions.RW.equals(param.systemPermission)) {
                try {
                    c8DB.grantDefaultCollectionAccess(USER_NAME_NEW, Permissions.RW);
                } catch (final C8DBException e) {
                    fail(details);
                }
            } else {
                try {
                    c8DB.grantDefaultCollectionAccess(USER_NAME_NEW, Permissions.RW);
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.deleteUser(USER_NAME_NEW);
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void createCollection() {
        try {
            if (Permissions.RW.equals(param.dbPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details,
                        c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo(),
                        is(notNullValue()));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
                    fail(details);
                } catch (final C8DBException e) {
                }
                try {
                    c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo();
                    fail(details);
                } catch (final C8DBException e) {
                }
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void dropCollection() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).createCollection(COLLECTION_NAME_NEW);
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).grantAccess(USER_NAME,
                    param.colPermission);
            if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
                } catch (final C8DBException e) {
                    fail(details);
                }
                try {
                    c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo();
                    fail(details);
                } catch (final C8DBException e) {
                }
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details,
                        c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).getInfo(),
                        is(notNullValue()));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME_NEW).drop();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void seeCollection() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                final Collection<CollectionEntity> collections = c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
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
            final Collection<CollectionEntity> collections = c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
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
                        c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getInfo(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getInfo();
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
                        c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getProperties(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getProperties();
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
                        details, c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                                .changeProperties(new CollectionPropertiesOptions().waitForSync(true)),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
            assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .getProperties().getWaitForSync(), is(true));
        } else {
            try {
                c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .changeProperties(new CollectionPropertiesOptions().waitForSync(true));
                fail(details);
            } catch (final C8DBException e) {
            }
            assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .getProperties().getWaitForSync(), is(false));
        }
    }

    @Test
    public void readCollectionIndexes() {
        if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
            try {
                assertThat(details,
                        c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getIndexes(),
                        is(notNullValue()));
            } catch (final C8DBException e) {
                fail(details);
            }
        } else {
            try {
                c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).getIndexes();
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
                    final IndexEntity createHashIndex = c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                            .collection(COLLECTION_NAME).ensureHashIndex(Arrays.asList("a"), new HashIndexOptions());
                    assertThat(details, createHashIndex, is(notNullValue()));
                    id = createHashIndex.getId();
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(2));
            } else {
                try {
                    final IndexEntity createHashIndex = c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                            .collection(COLLECTION_NAME).ensureHashIndex(Arrays.asList("a"), new HashIndexOptions());
                    id = createHashIndex.getId();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(1));
            }
        } finally {
            if (id != null) {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
            }
        }
    }

    @Test
    public void dropCollectionIndex() {
        final String id = c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                .ensureHashIndex(Arrays.asList("a"), new HashIndexOptions()).getId();
        try {
            if (Permissions.RW.equals(param.dbPermission) && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(1));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getIndexes().size(), is(2));
            }
        } finally {
            if (id != null) {
                try {
                    c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteIndex(id);
                } catch (final C8DBException e) {
                }
            }
        }
    }

    @Test
    public void truncateCollection() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).truncate();
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void readDocumentByKey() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
                assertThat(details, c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(notNullValue()));
            } else {
                assertThat(details, c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(nullValue()));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void readDocumentByAql() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && (Permissions.RW.equals(param.colPermission) || Permissions.RO.equals(param.colPermission))) {
                assertThat(details,
                        c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME)
                                .query("FOR i IN @@col RETURN i", new MapBuilder().put("@col", COLLECTION_NAME).get(),
                                        new C8qlQueryOptions(), BaseDocument.class)
                                .asListRemaining().size(),
                        is(1));
            } else {
                assertThat(details, c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class), is(nullValue()));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
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
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .insertDocument(new BaseDocument("123"));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                            .insertDocument(new BaseDocument("123"));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateDocumentByKey() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).updateDocument("123",
                            new BaseDocument(new MapBuilder().put("test", "test").get()));
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test").toString(), is("test"));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).updateDocument("123",
                            new BaseDocument(new MapBuilder().put("test", "test").get()));
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test"), is(nullValue()));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void updateDocumentByAql() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query(
                            "FOR i IN @@col UPDATE i WITH @newDoc IN @@col",
                            new MapBuilder().put("@col", COLLECTION_NAME)
                                    .put("newDoc", new BaseDocument(new MapBuilder().put("test", "test").get())).get(),
                            new C8qlQueryOptions(), Void.class);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test").toString(), is("test"));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query(
                            "FOR i IN @@col UPDATE i WITH @newDoc IN @@col",
                            new MapBuilder().put("@col", COLLECTION_NAME)
                                    .put("newDoc", new BaseDocument(new MapBuilder().put("test", "test").get())).get(),
                            new C8qlQueryOptions(), Void.class);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .getDocument("123", BaseDocument.class).getAttribute("test"), is(nullValue()));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteDocumentByKey() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

    @Test
    public void deleteDocumentByAql() {
        try {
            c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                    .insertDocument(new BaseDocument("123"));
            if ((Permissions.RW.equals(param.dbPermission) || Permissions.RO.equals(param.dbPermission))
                    && Permissions.RW.equals(param.colPermission)) {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query("REMOVE @key IN @@col",
                            new MapBuilder().put("key", "123").put("@col", COLLECTION_NAME).get(),
                            new C8qlQueryOptions(), Void.class);
                } catch (final C8DBException e) {
                    fail(details);
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(false));
            } else {
                try {
                    c8DB.db(C8Defaults.DEFAULT_TENANT, DB_NAME).query("REMOVE @key IN @@col",
                            new MapBuilder().put("key", "123").put("@col", COLLECTION_NAME).get(),
                            new C8qlQueryOptions(), Void.class);
                    fail(details);
                } catch (final C8DBException e) {
                }
                assertThat(details, c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME)
                        .documentExists("123"), is(true));
            }
        } finally {
            try {
                c8DBRoot.db(C8Defaults.DEFAULT_TENANT, DB_NAME).collection(COLLECTION_NAME).deleteDocument("123");
            } catch (final C8DBException e) {
            }
        }
    }

}
