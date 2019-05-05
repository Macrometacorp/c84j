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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DB;
import com.c8.C8DBException;
import com.c8.Protocol;
import com.c8.C8DB.Builder;
import com.c8.entity.C8DBVersion;
import com.c8.entity.LogEntity;
import com.c8.entity.LogLevel;
import com.c8.entity.LogLevelEntity;
import com.c8.entity.Permissions;
import com.c8.entity.UserEntity;
import com.c8.model.LogOptions;
import com.c8.model.UserCreateOptions;
import com.c8.model.UserUpdateOptions;
import com.c8.model.LogOptions.SortOrder;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;
import com.c8.velocystream.Response;

/**
 * @author Mark Vollmary
 * @author Reşat SABIQ
 */
@RunWith(Parameterized.class)
public class C8DBTest {

	@Parameters
	public static Collection<C8DB.Builder> builders() {
		return Arrays.asList(//
			new C8DB.Builder().useProtocol(Protocol.VST), //
			new C8DB.Builder().useProtocol(Protocol.HTTP_JSON), //
			new C8DB.Builder().useProtocol(Protocol.HTTP_VPACK) //
		);
	}

	private static final String ROOT = "root";
	private static final String USER = "mit dem mund";
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
	public void createDatabase() {
		try {
			final Boolean result = c8DB.createDatabase(BaseTest.TEST_DB);
			assertThat(result, is(true));
		} finally {
			try {
				c8DB.db(BaseTest.TEST_DB).drop();
			} catch (final C8DBException e) {
			}
		}
	}

	@Test
	public void deleteDatabase() {
		final Boolean resultCreate = c8DB.createDatabase(BaseTest.TEST_DB);
		assertThat(resultCreate, is(true));
		final Boolean resultDelete = c8DB.db(BaseTest.TEST_DB).drop();
		assertThat(resultDelete, is(true));
	}

	@Test
	public void getDatabases() {
		try {
			Collection<String> dbs = c8DB.getDatabases();
			assertThat(dbs, is(notNullValue()));
			assertThat(dbs.size(), is(greaterThan(0)));
			final int dbCount = dbs.size();
			assertThat(dbs.iterator().next(), is("_system"));
			c8DB.createDatabase(BaseTest.TEST_DB);
			dbs = c8DB.getDatabases();
			assertThat(dbs.size(), is(greaterThan(dbCount)));
			assertThat(dbs, hasItem("_system"));
			assertThat(dbs, hasItem(BaseTest.TEST_DB));
		} finally {
			c8DB.db(BaseTest.TEST_DB).drop();
		}
	}

	@Test
	public void getAccessibleDatabases() {
		final Collection<String> dbs = c8DB.getAccessibleDatabases();
		assertThat(dbs, is(notNullValue()));
		assertThat(dbs.size(), greaterThan(0));
		assertThat(dbs, hasItem("_system"));
	}

	@Test
	public void getAccessibleDatabasesFor() {
		final Collection<String> dbs = c8DB.getAccessibleDatabasesFor("root");
		assertThat(dbs, is(notNullValue()));
		assertThat(dbs.size(), greaterThan(0));
		assertThat(dbs, hasItem("_system"));
	}

	@Test
	public void createUser() {
		try {
			final UserEntity result = c8DB.createUser(USER, PW, null);
			assertThat(result, is(notNullValue()));
			assertThat(result.getUser(), is(USER));
		} finally {
			c8DB.deleteUser(USER);
		}
	}

	@Test
	public void deleteUser() {
		c8DB.createUser(USER, PW, null);
		c8DB.deleteUser(USER);
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
			matchers.add(is(USER));

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
		final C8DB c8DB = new C8DB.Builder().password("no").build();
		try {
			c8DB.getVersion();
			fail();
		} catch (final C8DBException e) {

		}
	}

	@Test
	public void authenticationFailUser() {
		final C8DB c8DB = new C8DB.Builder().user("no").build();
		try {
			c8DB.getVersion();
			fail();
		} catch (final C8DBException e) {

		}
	}

	@Test
	public void execute() throws VPackException {
		final Response response = c8DB.execute(new Request("_system", RequestType.GET, "/_api/version"));
		assertThat(response.getBody(), is(notNullValue()));
		assertThat(response.getBody().get("version").isString(), is(true));
	}

	@Test
	public void getLogs() {
		final LogEntity logs = c8DB.getLogs(null);
		assertThat(logs, is(notNullValue()));
		assertThat(logs.getTotalAmount(), greaterThan(0L));
		assertThat((long) logs.getLid().size(), is(logs.getTotalAmount()));
		assertThat((long) logs.getLevel().size(), is(logs.getTotalAmount()));
		assertThat((long) logs.getTimestamp().size(), is(logs.getTotalAmount()));
		assertThat((long) logs.getText().size(), is(logs.getTotalAmount()));
	}

	@Test
	public void getLogsUpto() {
		final LogEntity logsUpto = c8DB.getLogs(new LogOptions().upto(LogLevel.WARNING));
		assertThat(logsUpto, is(notNullValue()));
		assertThat(logsUpto.getLevel(), not(contains(LogLevel.INFO)));
	}

	@Test
	public void getLogsLevel() {
		final LogEntity logsInfo = c8DB.getLogs(new LogOptions().level(LogLevel.INFO));
		assertThat(logsInfo, is(notNullValue()));
		assertThat(logsInfo.getLevel(), everyItem(is(LogLevel.INFO)));
	}

	@Test
	public void getLogsStart() {
		final LogEntity logs = c8DB.getLogs(null);
		assertThat(logs.getLid(), not(empty()));
		final LogEntity logsStart = c8DB.getLogs(new LogOptions().start(logs.getLid().get(0) + 1));
		assertThat(logsStart, is(notNullValue()));
		assertThat(logsStart.getLid(), not(contains(logs.getLid().get(0))));
	}

	@Test
	public void getLogsSize() {
		final LogEntity logs = c8DB.getLogs(null);
		assertThat(logs.getLid().size(), greaterThan(0));
		final LogEntity logsSize = c8DB.getLogs(new LogOptions().size(logs.getLid().size() - 1));
		assertThat(logsSize, is(notNullValue()));
		assertThat(logsSize.getLid().size(), is(logs.getLid().size() - 1));
	}

	@Test
	public void getLogsOffset() {
		final LogEntity logs = c8DB.getLogs(null);
		assertThat(logs.getTotalAmount(), greaterThan(0L));
		final LogEntity logsOffset = c8DB.getLogs(new LogOptions().offset(1));
		assertThat(logsOffset, is(notNullValue()));
		assertThat(logsOffset.getLid(), not(hasItem(logs.getLid().get(0))));
	}

	@Test
	public void getLogsSearch() {
		final LogEntity logs = c8DB.getLogs(null);
		final LogEntity logsSearch = c8DB.getLogs(new LogOptions().search(BaseTest.TEST_DB));
		assertThat(logsSearch, is(notNullValue()));
		assertThat(logs.getTotalAmount(), greaterThan(logsSearch.getTotalAmount()));
	}

	@Test
	public void getLogsSortAsc() {
		final LogEntity logs = c8DB.getLogs(new LogOptions().sort(SortOrder.asc));
		assertThat(logs, is(notNullValue()));
		long lastId = -1;
		for (final Long id : logs.getLid()) {
			assertThat(id, greaterThan(lastId));
			lastId = id;
		}
	}

	@Test
	public void getLogsSortDesc() {
		final LogEntity logs = c8DB.getLogs(new LogOptions().sort(SortOrder.desc));
		assertThat(logs, is(notNullValue()));
		long lastId = Long.MAX_VALUE;
		for (final Long id : logs.getLid()) {
			assertThat(lastId, greaterThan(id));
			lastId = id;
		}
	}

	@Test
	public void getLogLevel() {
		final LogLevelEntity logLevel = c8DB.getLogLevel();
		assertThat(logLevel, is(notNullValue()));
		assertThat(logLevel.getAgency(), is(LogLevelEntity.LogLevel.INFO));
	}

	@Test
	public void setLogLevel() {
		final LogLevelEntity entity = new LogLevelEntity();
		try {
			entity.setAgency(LogLevelEntity.LogLevel.ERROR);
			final LogLevelEntity logLevel = c8DB.setLogLevel(entity);
			assertThat(logLevel, is(notNullValue()));
			assertThat(logLevel.getAgency(), is(LogLevelEntity.LogLevel.ERROR));
		} finally {
			entity.setAgency(LogLevelEntity.LogLevel.INFO);
			c8DB.setLogLevel(entity);
		}
	}

	@Test
	public void c8DBException() {
		try {
			c8DB.db("no").getInfo();
			fail();
		} catch (final C8DBException e) {
			assertThat(e.getResponseCode(), is(404));
			assertThat(e.getErrorNum(), is(1228));
			assertThat(e.getErrorMessage(), is("database not found"));
		}
	}

	@Test
	public void fallbackHost() {
		final C8DB c8DB = new C8DB.Builder().host("not-accessible", 8529).host("127.0.0.1", 8529).build();
		final C8DBVersion version = c8DB.getVersion();
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
		try {
			c8DB.createDatabase("db1");
			c8DB.createDatabase("db2");

			final C8DBVersion version1 = c8DB.db("db1").getVersion();
			assertThat(version1, is(notNullValue()));
			final C8DBVersion version2 = c8DB.db("db2").getVersion();
			assertThat(version2, is(notNullValue()));
		} finally {
			c8DB.db("db1").drop();
			c8DB.db("db2").drop();
		}
	}

	@Test
	public void acquireHostList() {
		final C8DB c8 = new C8DB.Builder().acquireHostList(true).build();
		final C8DBVersion version = c8.getVersion();
		assertThat(version, is(notNullValue()));
	}
}
