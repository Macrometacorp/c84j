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

import com.c8db.C8DB.Builder;
import com.c8db.entity.StreamAccessLevel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8UserTest extends BaseTest {

    private static final String STREAM_NAME = "dbCollectionTest";

    public C8UserTest(final Builder builder) {
        super(builder);
    }

    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    @Test
    public void getBacklog() {
        StreamAccessLevel apiKeyStreamAccess = db.user().getStreamAccessLevel("key", STREAM_NAME);
        assertThat(apiKeyStreamAccess, is(notNullValue()));
    }


    
    @AfterClass
    public static void shutdown() {
        db.drop();
    }
}
