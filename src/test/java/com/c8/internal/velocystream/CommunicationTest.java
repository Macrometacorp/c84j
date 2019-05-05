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

package com.c8.internal.velocystream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.c8.C8DB;
import com.c8.C8DBException;
import com.c8.C8Database;
import com.c8.entity.C8DBVersion;

/**
 * @author Mark Vollmary
 *
 */
public class CommunicationTest {

	private static final String FAST = "fast";
	private static final String SLOW = "slow";

	@Test
	public void chunkSizeSmall() {
		final C8DB c8DB = new C8DB.Builder().chunksize(20).build();
		final C8DBVersion version = c8DB.getVersion();
		assertThat(version, is(notNullValue()));
	}

	@Test
	public void multiThread() throws Exception {
		final C8DB c8DB = new C8DB.Builder().build();
		c8DB.getVersion();// authentication

		final Collection<String> result = new ConcurrentLinkedQueue<String>();
		final Thread fast = new Thread() {
			@Override
			public void run() {
				try {
					c8DB.db().query("return sleep(1)", null, null, null);
					result.add(FAST);
				} catch (final C8DBException e) {
				}
			}
		};
		final Thread slow = new Thread() {
			@Override
			public void run() {
				try {
					c8DB.db().query("return sleep(4)", null, null, null);
					result.add(SLOW);
				} catch (final C8DBException e) {
				}
			}
		};
		slow.start();
		Thread.sleep(1000);
		fast.start();

		slow.join();
		fast.join();

		assertThat(result.size(), is(2));
		final Iterator<String> iterator = result.iterator();
		assertThat(iterator.next(), is(FAST));
		assertThat(iterator.next(), is(SLOW));
	}

	@Test
	public void multiThreadSameDatabases() throws Exception {
		final C8DB c8DB = new C8DB.Builder().build();
		c8DB.getVersion();// authentication

		final C8Database db = c8DB.db();

		final Collection<String> result = new ConcurrentLinkedQueue<String>();
		final Thread t1 = new Thread() {
			@Override
			public void run() {
				try {
					db.query("return sleep(1)", null, null, null);
					result.add("1");
				} catch (final C8DBException e) {
					e.printStackTrace(System.err);
				}
			}
		};
		final Thread t2 = new Thread() {
			@Override
			public void run() {
				try {
					db.query("return sleep(1)", null, null, null);
					result.add("1");
				} catch (final C8DBException e) {
					e.printStackTrace(System.err);
				}
			}
		};
		t2.start();
		t1.start();
		t2.join();
		t1.join();
		assertThat(result.size(), is(2));
	}

	@Test
	public void multiThreadMultiDatabases() throws Exception {
		final C8DB c8DB = new C8DB.Builder().build();
		c8DB.getVersion();// authentication

		try {
			c8DB.createDatabase("db1");
			c8DB.createDatabase("db2");
			final C8Database db1 = c8DB.db("db1");
			final C8Database db2 = c8DB.db("db2");

			final Collection<String> result = new ConcurrentLinkedQueue<String>();
			final Thread t1 = new Thread() {
				@Override
				public void run() {
					try {
						db1.query("return sleep(1)", null, null, null);
						result.add("1");
					} catch (final C8DBException e) {
					}
				}
			};
			final Thread t2 = new Thread() {
				@Override
				public void run() {
					try {
						db2.query("return sleep(1)", null, null, null);
						result.add("1");
					} catch (final C8DBException e) {
					}
				}
			};
			t2.start();
			t1.start();
			t2.join();
			t1.join();
			assertThat(result.size(), is(2));
		} finally {
			c8DB.db("db1").drop();
			c8DB.db("db2").drop();
		}
	}

	@Test
	public void minOneConnection() {
		final C8DB c8DB = new C8DB.Builder().maxConnections(0).build();
		final C8DBVersion version = c8DB.getVersion();
		assertThat(version, is(notNullValue()));
	}

	@Test
	public void defaultMaxConnection() {
		final C8DB c8DB = new C8DB.Builder().maxConnections(null).build();
		final C8DBVersion version = c8DB.getVersion();
		assertThat(version, is(notNullValue()));
	}
}
