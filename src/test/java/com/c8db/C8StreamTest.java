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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.c8db.C8DB.Builder;
import com.c8db.entity.C8StreamBacklogEntity;
import com.c8db.entity.C8StreamEntity;
import com.c8db.entity.C8StreamStatisticsEntity;

/**
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8StreamTest extends BaseTest {

    private static final String STREAM_NAME = "dbCollectionTest";

    public C8StreamTest(final Builder builder) {
        super(builder);
    }

    @Before
    public void setup() {
        try {
            db.stream(STREAM_NAME);
        } catch (final C8DBException e1) {
        }
    }

    @After
    public void teardown() {
    }

    @Test
    public void getBacklog() {
        C8StreamBacklogEntity backlog = db.stream(STREAM_NAME).getBacklog(false);
        assertThat(backlog, is(notNullValue()));
        assertThat(backlog.getTopicName().contains(STREAM_NAME), is(true));
    }

    @Test
    public void getStreamStatistics() {
        C8StreamStatisticsEntity statistics = db.stream("_polog").getStatistics(false);
        assertThat(statistics, is(notNullValue()));
    }
    
    @Ignore
    @Test
    public void streamsTest() {
        C8Stream stream = db.stream("test");
        Collection<C8StreamEntity> streams = db.getPersistentStreams(null);
        stream.getBacklog(false);
        stream.expireMessagesInSeconds("c8gui_51469", 10, true);
        Collection<String> subs = stream.getSubscriptions(true);
        
        stream.resetCursorToTimestamp("c8gui_51469", (int) System.currentTimeMillis(), true);
        stream.skipMessages("c8gui_51469", 2, true);
        stream.skipAllMessages("c8gui_51469", true);
       // stream.resetCursor("c8gui_51469", true);
        stream.deleteSubscription("c8gui_51469", false);
        stream.terminate(true);
    }
    
    
    @AfterClass
    public static void shutdown() {
        db.drop();
    }
}
