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

package com.c8;

import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.runners.Parameterized.Parameters;

import com.c8.C8DB;
import com.c8.C8DBException;
import com.c8.C8Database;
import com.c8.Protocol;

/**
 * 
 *
 */
public abstract class BaseTest {

	@Parameters
	public static Collection<C8DB.Builder> builders() {
		return Arrays.asList(//
			new C8DB.Builder().useProtocol(Protocol.VST), //
			new C8DB.Builder().useProtocol(Protocol.HTTP_JSON), //
			new C8DB.Builder().useProtocol(Protocol.HTTP_VPACK) //
		);
	}

	protected static final String TEST_DB = "java_driver_test_db";
	protected static C8DB c8DB;
	protected static C8Database db;

	public BaseTest(final C8DB.Builder builder) {
		super();
		if (c8DB != null) {
			shutdown();
		}
		c8DB = builder.build();
		try {
			c8DB.db(TEST_DB).drop();
		} catch (final C8DBException e) {
		}
		c8DB.createDatabase(TEST_DB);
		db = c8DB.db(TEST_DB);
	}

	@AfterClass
	public static void shutdown() {
		c8DB.db(TEST_DB).drop();
		c8DB.shutdown();
		c8DB = null;
	}

	protected boolean requireVersion(final int major, final int minor) {
		final String[] split = c8DB.getVersion().getVersion().split("\\.");
		return Integer.valueOf(split[0]) >= major && Integer.valueOf(split[1]) >= minor;
	}

}
