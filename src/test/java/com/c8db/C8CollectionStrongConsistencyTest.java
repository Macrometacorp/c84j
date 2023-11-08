/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.CollectionEntity;
import com.c8db.entity.CollectionPropertiesEntity;
import com.c8db.entity.CollectionType;
import com.c8db.entity.DatabaseMetadataEntity;
import com.c8db.entity.DocumentCreateEntity;
import com.c8db.entity.DocumentDeleteEntity;
import com.c8db.entity.DocumentUpdateEntity;
import com.c8db.entity.IndexEntity;
import com.c8db.entity.MultiDocumentEntity;
import com.c8db.model.CollectionCountOptions;
import com.c8db.model.CollectionCreateOptions;
import com.c8db.model.CollectionDropOptions;
import com.c8db.model.CollectionIndexDeleteOptions;
import com.c8db.model.CollectionIndexReadOptions;
import com.c8db.model.CollectionIndexesReadOptions;
import com.c8db.model.CollectionsReadOptions;
import com.c8db.model.DocumentCreateOptions;
import com.c8db.model.DocumentDeleteOptions;
import com.c8db.model.DocumentExistsOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.DocumentReplaceOptions;
import com.c8db.model.DocumentUpdateOptions;
import com.c8db.model.FulltextIndexOptions;
import com.c8db.model.GeoIndexOptions;
import com.c8db.model.HashIndexOptions;
import com.c8db.model.PersistentIndexOptions;
import com.c8db.model.SkiplistIndexOptions;
import com.c8db.model.TTLIndexOptions;
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
public class C8CollectionStrongConsistencyTest extends BaseTest {

    private static String COLLECTION_NAME = "doc_tests_1";

    private static String SOURCE_REGION_HEADER = "X-Gdn-Source-Region";
    private static String host;
    private static String spotDc;
    private static TestC8DBBuilder testC8DBBuilder;

    @Parameterized.Parameters
    public static Collection<C8DB.Builder> builders() {
        testC8DBBuilder = new TestC8DBBuilder();
        testC8DBBuilder.useProtocol(Protocol.HTTP_JSON);
        return Arrays.asList(testC8DBBuilder);
    }

    public C8CollectionStrongConsistencyTest(final Builder builder) {
        super(builder);
        host = builder.getHosts().iterator().next().getHost();
    }

    @Test
    public void test00_check_if_non_spot_host() {
        DatabaseMetadataEntity metadataEntity = db.getMetadata();
        spotDc = metadataEntity.getOptions().getSpotDc();
        assertFalse("The host `" + host+ "` is the same as spot host `" + spotDc
                        + "`. This test suite requires connection to non-spot host for database " + TEST_DB,
                host.contains(spotDc));
    }

    @Test
    public void test01_create_collection_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        CollectionCreateOptions options = new CollectionCreateOptions()
                .type(CollectionType.DOCUMENT)
                .strongConsistency(true)
                .waitForSync(true);
        CollectionEntity coll = db.createCollection(COLLECTION_NAME, options);

        // validate that collection created with strong consistency
        assertEquals(coll.getName(), COLLECTION_NAME);
        assertTrue(coll.getStrongConsistency());

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate that collection created with strong consistency by another call
        CollectionEntity coll2 = db.collection(COLLECTION_NAME).getInfo();
        assertEquals(coll2.getName(), COLLECTION_NAME);
        assertTrue(coll2.getStrongConsistency());
    }

    @Test
    public void test02_create_all_indexes_with_strong_consistency() {

        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        }, 6);

        // fulltext
        FulltextIndexOptions options = new FulltextIndexOptions()
                .name("fulltextIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity = db.collection(COLLECTION_NAME).ensureFulltextIndex(Arrays.asList("property1"),
                options);

        // validate
        assertEquals(indexEntity.getName(), "fulltextIndex1");
        assertEquals(indexEntity.getFields().size(), 1);
        assertEquals(indexEntity.getFields().iterator().next(), "property1");

        // hash
        HashIndexOptions options2 = new HashIndexOptions()
                .name("hashIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity2 = db.collection(COLLECTION_NAME).ensureHashIndex(Arrays.asList("property2"),
                options2);

        // validate
        assertEquals(indexEntity2.getName(), "hashIndex1");
        assertEquals(indexEntity2.getFields().size(), 1);
        assertEquals(indexEntity2.getFields().iterator().next(), "property2");

        // geo
        GeoIndexOptions options3 = new GeoIndexOptions()
                .name("geoIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity3 = db.collection(COLLECTION_NAME).ensureGeoIndex(Arrays.asList("property3"),
                options3);

        // validate
        assertEquals(indexEntity3.getName(), "geoIndex1");
        assertEquals(indexEntity3.getFields().size(), 1);
        assertEquals(indexEntity3.getFields().iterator().next(), "property3");

        // ttl
        TTLIndexOptions options4 = new TTLIndexOptions()
                .name("ttlIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity4 = db.collection(COLLECTION_NAME).ensureTTLIndex(Arrays.asList("property4"),
                options4);

        // validate
        assertEquals(indexEntity4.getName(), "ttlIndex1");
        assertEquals(indexEntity4.getFields().size(), 1);
        assertEquals(indexEntity4.getFields().iterator().next(), "property4");

        // skiplist
        SkiplistIndexOptions options5 = new SkiplistIndexOptions()
                .name("skiplistIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity5 = db.collection(COLLECTION_NAME).ensureSkiplistIndex(Arrays.asList("property5"),
                options5);

        // validate
        assertEquals(indexEntity5.getName(), "skiplistIndex1");
        assertEquals(indexEntity5.getFields().size(), 1);
        assertEquals(indexEntity5.getFields().iterator().next(), "property5");

        // persistent
        PersistentIndexOptions options6 = new PersistentIndexOptions()
                .name("persistentIndex1")
                .strongConsistency(true)
                .inBackground(false);
        IndexEntity indexEntity6 = db.collection(COLLECTION_NAME).ensurePersistentIndex(Arrays.asList("property6"),
                options6);

        // validate
        assertEquals(indexEntity6.getName(), "persistentIndex1");
        assertEquals(indexEntity6.getFields().size(), 1);
        assertEquals(indexEntity6.getFields().iterator().next(), "property6");
    }

    @Test
    public void test02_get_collections_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        CollectionsReadOptions options = new CollectionsReadOptions()
                .strongConsistency(true);
        Collection<CollectionEntity> list = db.getCollections(options);

        // validate
        assertTrue(list.stream().anyMatch(e -> e.getName().equals(COLLECTION_NAME)));
    }

    @Test
    public void test03_get_indexes_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        CollectionIndexesReadOptions options = new CollectionIndexesReadOptions()
                .strongConsistency(true);
        Collection<IndexEntity> list = db.collection(COLLECTION_NAME).getIndexes(options);

        // validate
        String[] keyArr = list.stream().map(IndexEntity::getName).collect(Collectors.toList()).toArray(new String[]{});
        Arrays.sort(keyArr);
        assertArrayEquals(keyArr, new String[]{"fulltextIndex1", "geoIndex1", "hashIndex1", "persistentIndex1",
                "primary", "skiplistIndex1", "ttlIndex1"});
    }

    @Test
    public void test03_create_document_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentCreateOptions options = new DocumentCreateOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc1 = new Doc("key1", "property 1");
        DocumentCreateEntity<Doc> res = coll.insertDocument(doc1, options);

        // validate
        assertEquals(res.getKey(), "key1");
    }

    @Test
    public void test04_get_and_delete_index_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        },2);

        CollectionIndexReadOptions options0 = new CollectionIndexReadOptions()
                .strongConsistency(true);
        IndexEntity indexEntity = db.collection(COLLECTION_NAME).getIndex("fulltextIndex1", options0);

        assertEquals("fulltextIndex1", indexEntity.getName());

        CollectionIndexDeleteOptions options = new CollectionIndexDeleteOptions()
                .strongConsistency(true);
        String id = db.collection(COLLECTION_NAME).deleteIndex("fulltextIndex1", options);

        // validate
        assertEquals(indexEntity.getId(), id);
    }

    @Test
    public void test04_create_multiple_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentCreateOptions options = new DocumentCreateOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc2 = new Doc("key2", "property 2");
        Doc doc3 = new Doc("key3", "property 3");
        MultiDocumentEntity<DocumentCreateEntity<Doc>> res = coll.insertDocuments(Arrays.asList(doc2, doc3), options);

        // validate
        assertEquals(res.getDocuments().size(), 2);
    }

    @Test
    public void test05_count_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        CollectionCountOptions options = new CollectionCountOptions()
                .strongConsistency(true);
        CollectionPropertiesEntity collectionPropertiesEntity = db.collection(COLLECTION_NAME)
                .count(options);

        // validate
        assertEquals((long) collectionPropertiesEntity.getCount(), 3L);
    }

    @Test
    public void test05_read_document_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentReadOptions options = new DocumentReadOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc res = coll.getDocument("key1", Doc.class, options);

        // validate
        assertEquals(res.getKey(), "key1");
        assertEquals(res.getProperty1(), "property 1");
    }

    @Test
    public void test06_read_multiple_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentReadOptions options = new DocumentReadOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        MultiDocumentEntity<Doc> res = coll.getDocuments(Arrays.asList("key2", "key3"), Doc.class, options);

        // validate
        Doc doc2 = res.getDocuments().stream().filter(d -> d.getKey().equals("key2")).findFirst().get();
        assertEquals(doc2.getProperty1(), "property 2");
        Doc doc3 = res.getDocuments().stream().filter(d -> d.getKey().equals("key3")).findFirst().get();
        assertEquals(doc3.getProperty1(), "property 3");
    }

    @Test
    public void test07_replace_document_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentReplaceOptions options = new DocumentReplaceOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc1 = new Doc("key1", "replaced property 1");
        DocumentUpdateEntity<Doc> res = coll.replaceDocument("key1", doc1, options);
        assertEquals(res.getKey(), "key1");

        // validate
        DocumentReadOptions options2 = new DocumentReadOptions()
                .strongConsistency(true);
        Doc res2 = coll.getDocument("key1", Doc.class, options2);
        assertEquals(res2.getProperty1(), "replaced property 1");
    }

    @Test
    public void test08_replace_multiple_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentReplaceOptions options = new DocumentReplaceOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc2 = new Doc("key2", "replaced property 2");
        Doc doc3 = new Doc("key3", "replaced property 3");
        MultiDocumentEntity<DocumentUpdateEntity<Doc>> res = coll.replaceDocuments(Arrays.asList(doc2, doc3), options);

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate if updated with `get documents` call
        DocumentReadOptions options2 = new DocumentReadOptions()
                .strongConsistency(true);
        MultiDocumentEntity<Doc> res2 = coll.getDocuments(Arrays.asList("key2", "key3"), Doc.class, options2);
        Doc doc_2 = res2.getDocuments().stream().filter(d -> d.getKey().equals("key2")).findFirst().get();
        assertEquals("replaced property 2", doc_2.getProperty1());
        Doc doc_3 = res2.getDocuments().stream().filter(d -> d.getKey().equals("key3")).findFirst().get();
        assertEquals("replaced property 3", doc_3.getProperty1());
    }

    @Test
    public void test09_update_document_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentUpdateOptions options = new DocumentUpdateOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc1 = new Doc("key1", "updated property 1");
        DocumentUpdateEntity<Doc> res = coll.updateDocument("key1", doc1, options);

        // validate
        assertEquals(res.getKey(), "key1");
        DocumentReadOptions options2 = new DocumentReadOptions()
                .strongConsistency(true);
        Doc res2 = coll.getDocument("key1", Doc.class, options2);
        assertEquals(res2.getProperty1(), "updated property 1");
    }

    @Test
    public void test10_update_multiple_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentUpdateOptions options = new DocumentUpdateOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        Doc doc2 = new Doc("key2", "updated property 2");
        Doc doc3 = new Doc("key3", "updated property 3");
        MultiDocumentEntity<DocumentUpdateEntity<Doc>> res = coll.updateDocuments(Arrays.asList(doc2, doc3), options);

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate if updated with `get documents` call
        DocumentReadOptions options2 = new DocumentReadOptions()
                .strongConsistency(true);
        MultiDocumentEntity<Doc> res2 = coll.getDocuments(Arrays.asList("key2", "key3"), Doc.class, options2);
        Doc doc_2 = res2.getDocuments().stream().filter(d -> d.getKey().equals("key2")).findFirst().get();
        assertEquals("updated property 2", doc_2.getProperty1());
        Doc doc_3 = res2.getDocuments().stream().filter(d -> d.getKey().equals("key3")).findFirst().get();
        assertEquals("updated property 3", doc_3.getProperty1());
    }

    @Test
    public void test11_delete_document_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        }, 2);

        DocumentDeleteOptions options = new DocumentDeleteOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        DocumentDeleteEntity<BaseDocument> res = coll.deleteDocument("key1", BaseDocument.class, options);

        // validate and test
        assertEquals(res.getKey(), "key1");
        DocumentExistsOptions options2 = new DocumentExistsOptions()
                .strongConsistency(true);
        Boolean res2 = coll.documentExists("key1", options2);
        assertFalse(res2);
    }

    @Test
    public void test12_delete_multiple_documents_with_strong_consistency() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        DocumentDeleteOptions options = new DocumentDeleteOptions()
                .strongConsistency(true);
        C8Collection coll = db.collection(COLLECTION_NAME);
        MultiDocumentEntity<DocumentDeleteEntity<Doc>> res =
                coll.deleteDocuments(Arrays.asList("key2", "key3"), Doc.class, options);

        // wait some time for replication
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {}
        // validate if updated with `get documents` call
        DocumentReadOptions options2 = new DocumentReadOptions()
                .strongConsistency(true);
        MultiDocumentEntity<Doc> res2 = coll.getDocuments(Arrays.asList("key2", "key3"), Doc.class, options2);
        assertEquals(0, res2.getDocuments().size());
    }

    @Test
    public void test13_drop() {
        //validate response
        testC8DBBuilder.listenResponse(response -> {
            assertEquals(spotDc, response.getMeta().get(SOURCE_REGION_HEADER));
        });

        CollectionDropOptions options = new CollectionDropOptions()
                .strongConsistency(true);
        db.collection(COLLECTION_NAME).drop(options);
    }

    public static class Doc extends BaseDocument {
        private String property1;

        public Doc() {}

        public Doc(String _key, String property1) {
            super(_key);
            this.property1 = property1;
        }

        public String getProperty1() {
            return property1;
        }
    }

}
