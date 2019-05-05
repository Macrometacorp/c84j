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

package com.c8.example;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.c8.C8Collection;
import com.c8.C8DB;
import com.c8.C8DBException;
import com.c8.C8Database;

/**
 * @author Mark Vollmary
 *
 */
public class ExampleBase {

	protected static final String DB_NAME = "json_example_db";
	protected static final String COLLECTION_NAME = "json_example_collection";

	protected static C8DB c8DB;
	protected static C8Database db;
	protected static C8Collection collection;

	@BeforeClass
	public static void setUp() {
		c8DB = new C8DB.Builder().build();
		try {
			c8DB.db(DB_NAME).drop();
		} catch (final C8DBException e) {
		}
		c8DB.createDatabase(DB_NAME);
		db = c8DB.db(DB_NAME);
		db.createCollection(COLLECTION_NAME);
		collection = db.collection(COLLECTION_NAME);
	}

	@AfterClass
	public static void tearDown() {
		db.drop();
		c8DB.shutdown();
	}

}
