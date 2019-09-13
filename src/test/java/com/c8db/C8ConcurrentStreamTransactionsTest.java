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

import static org.junit.Assume.assumeTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.c8db.C8DB.Builder;
import com.c8db.entity.BaseDocument;
import com.c8db.entity.StreamTransactionEntity;
import com.c8db.model.DocumentCreateOptions;
import com.c8db.model.StreamTransactionOptions;

/**
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8ConcurrentStreamTransactionsTest extends BaseTest {

    private static final String COLLECTION_NAME = "db_concurrent_stream_transactions_test";

    public C8ConcurrentStreamTransactionsTest(final Builder builder) {
        super(builder);
        try {
            if (db.collection(COLLECTION_NAME).exists())
                db.collection(COLLECTION_NAME).drop();

            db.createCollection(COLLECTION_NAME, null);
        } catch (final C8DBException e) {

        }
    }

    @After
    public void teardown() {
        try {
            db.collection(COLLECTION_NAME).drop();
        } catch (final C8DBException e) {
        }
    }

    @Test
    public void conflictOnInsertDocumentWithNotYetCommittedTx() {
        assumeTrue(requireSingleServer());
        assumeTrue(requireVersion(3, 5));

        StreamTransactionEntity tx1 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME));

        StreamTransactionEntity tx2 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME));

        String key = UUID.randomUUID().toString();

        // insert a document from within tx1
        db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                new DocumentCreateOptions().streamTransactionId(tx1.getId()));

        try {
            // insert conflicting document from within tx2
            db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                    new DocumentCreateOptions().streamTransactionId(tx2.getId()));

            throw new RuntimeException("This should never be thrown");
        } catch (C8DBException e) {
            e.printStackTrace();
        }

        db.abortStreamTransaction(tx1.getId());
        db.abortStreamTransaction(tx2.getId());
    }

    @Test
    public void conflictOnInsertDocumentWithAlreadyCommittedTx() {
        assumeTrue(requireSingleServer());
        assumeTrue(requireVersion(3, 5));

        StreamTransactionEntity tx1 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME));

        StreamTransactionEntity tx2 = db.beginStreamTransaction(
                new StreamTransactionOptions().readCollections(COLLECTION_NAME).writeCollections(COLLECTION_NAME));

        String key = UUID.randomUUID().toString();

        // insert a document from within tx1
        db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                new DocumentCreateOptions().streamTransactionId(tx1.getId()));

        // commit tx1
        db.commitStreamTransaction(tx1.getId());

        try {
            // insert conflicting document from within tx2
            db.collection(COLLECTION_NAME).insertDocument(new BaseDocument(key),
                    new DocumentCreateOptions().streamTransactionId(tx2.getId()));

            throw new RuntimeException("This should never be thrown");
        } catch (C8DBException e) {
            e.printStackTrace();
            db.abortStreamTransaction(tx2.getId());
        }
    }
}
