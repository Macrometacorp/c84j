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

package com.c8.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DB;
import com.c8.C8DBException;
import com.c8.C8Database;
import com.c8.Protocol;
import com.c8.entity.C8DBVersion;
import com.c8.entity.LogEntity;
import com.c8.entity.LogLevelEntity;
import com.c8.entity.Permissions;
import com.c8.entity.ServerRole;
import com.c8.entity.UserEntity;
import com.c8.internal.C8Executor.ResponseDeserializer;
import com.c8.internal.http.HttpCommunication;
import com.c8.internal.http.HttpProtocol;
import com.c8.internal.net.CommunicationProtocol;
import com.c8.internal.net.HostHandle;
import com.c8.internal.net.HostResolver;
import com.c8.internal.net.HostResolver.EndpointResolver;
import com.c8.internal.util.C8SerializationFactory;
import com.c8.internal.util.C8SerializationFactory.Serializer;
import com.c8.internal.velocystream.VstCommunicationSync;
import com.c8.internal.velocystream.VstProtocol;
import com.c8.model.LogOptions;
import com.c8.model.UserCreateOptions;
import com.c8.model.UserUpdateOptions;
import com.c8.util.C8CursorInitializer;
import com.c8.util.C8Serialization;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public class C8DBImpl extends InternalC8DB<C8ExecutorSync> implements C8DB {

	private C8CursorInitializer cursorInitializer;
	private CommunicationProtocol cp;

	public C8DBImpl(final VstCommunicationSync.Builder vstBuilder, final HttpCommunication.Builder httpBuilder,
		final C8SerializationFactory util, final Protocol protocol, final HostResolver hostResolver,
		final C8Context context) {
		super(new C8ExecutorSync(createProtocol(vstBuilder, httpBuilder, util.get(Serializer.INTERNAL), protocol),
				util, new DocumentCache()), util, context);
		cp = createProtocol(new VstCommunicationSync.Builder(vstBuilder).maxConnections(1),
			new HttpCommunication.Builder(httpBuilder), util.get(Serializer.INTERNAL), protocol);
		hostResolver.init(new EndpointResolver() {
			@Override
			public Collection<String> resolve(final boolean closeConnections) throws C8DBException {
				Collection<String> response;
				try {
					response = executor.execute(new Request(C8RequestParam.SYSTEM, RequestType.GET, PATH_ENDPOINTS),
						new ResponseDeserializer<Collection<String>>() {
							@Override
							public Collection<String> deserialize(final Response response) throws VPackException {
								final VPackSlice field = response.getBody().get("endpoints");
								Collection<String> endpoints;
								if (field.isNone()) {
									endpoints = Collections.<String> emptyList();
								} else {
									final Collection<Map<String, String>> tmp = util().deserialize(field,
										Collection.class);
									endpoints = new ArrayList<String>();
									for (final Map<String, String> map : tmp) {
										for (final String value : map.values()) {
											endpoints.add(value);
										}
									}
								}
								return endpoints;
							}
						}, null);
				} catch (final C8DBException e) {
					final Integer responseCode = e.getResponseCode();
					if (responseCode != null && responseCode == 403) {
						response = Collections.<String> emptyList();
					} else {
						throw e;
					}
				} finally {
					if (closeConnections) {
						C8DBImpl.this.shutdown();
					}
				}
				return response;
			}
		});
	}

	private static CommunicationProtocol createProtocol(
		final VstCommunicationSync.Builder vstBuilder,
		final HttpCommunication.Builder httpBuilder,
		final C8Serialization util,
		final Protocol protocol) {
		return (protocol == null || Protocol.VST == protocol) ? createVST(vstBuilder, util)
				: createHTTP(httpBuilder, util);
	}

	private static CommunicationProtocol createVST(
		final VstCommunicationSync.Builder builder,
		final C8Serialization util) {
		return new VstProtocol(builder.build(util));
	}

	private static CommunicationProtocol createHTTP(
		final HttpCommunication.Builder builder,
		final C8Serialization util) {
		return new HttpProtocol(builder.build(util));
	}

	@Override
	protected C8ExecutorSync executor() {
		return executor;
	}

	@Override
	public void shutdown() throws C8DBException {
		try {
			executor.disconnect();
			cp.close();
		} catch (final IOException e) {
			throw new C8DBException(e);
		}
	}

	@Override
	public C8Database db() {
		return db(C8RequestParam.SYSTEM);
	}

	@Override
	public C8Database db(final String name) {
		return new C8DatabaseImpl(this, name).setCursorInitializer(cursorInitializer);
	}

	@Override
	public Boolean createDatabase(final String name) throws C8DBException {
		return executor.execute(createDatabaseRequest(name), createDatabaseResponseDeserializer());
	}

	@Override
	public Collection<String> getDatabases() throws C8DBException {
		return executor.execute(getDatabasesRequest(db().name()), getDatabaseResponseDeserializer());
	}

	@Override
	public Collection<String> getAccessibleDatabases() throws C8DBException {
		return db().getAccessibleDatabases();
	}

	@Override
	public Collection<String> getAccessibleDatabasesFor(final String user) throws C8DBException {
		return executor.execute(getAccessibleDatabasesForRequest(db().name(), user),
			getAccessibleDatabasesForResponseDeserializer());
	}

	@Override
	public C8DBVersion getVersion() throws C8DBException {
		return db().getVersion();
	}

	@Override
	public ServerRole getRole() throws C8DBException {
		return executor.execute(getRoleRequest(), getRoleResponseDeserializer());
	}

	@Override
	public UserEntity createUser(final String user, final String passwd) throws C8DBException {
		return executor.execute(createUserRequest(db().name(), user, passwd, new UserCreateOptions()),
			UserEntity.class);
	}

	@Override
	public UserEntity createUser(final String user, final String passwd, final UserCreateOptions options)
			throws C8DBException {
		return executor.execute(createUserRequest(db().name(), user, passwd, options), UserEntity.class);
	}

	@Override
	public void deleteUser(final String user) throws C8DBException {
		executor.execute(deleteUserRequest(db().name(), user), Void.class);
	}

	@Override
	public UserEntity getUser(final String user) throws C8DBException {
		return executor.execute(getUserRequest(db().name(), user), UserEntity.class);
	}

	@Override
	public Collection<UserEntity> getUsers() throws C8DBException {
		return executor.execute(getUsersRequest(db().name()), getUsersResponseDeserializer());
	}

	@Override
	public UserEntity updateUser(final String user, final UserUpdateOptions options) throws C8DBException {
		return executor.execute(updateUserRequest(db().name(), user, options), UserEntity.class);
	}

	@Override
	public UserEntity replaceUser(final String user, final UserUpdateOptions options) throws C8DBException {
		return executor.execute(replaceUserRequest(db().name(), user, options), UserEntity.class);
	}

	@Override
	public void grantDefaultDatabaseAccess(final String user, final Permissions permissions) throws C8DBException {
		executor.execute(updateUserDefaultDatabaseAccessRequest(user, permissions), Void.class);
	}

	@Override
	public void grantDefaultCollectionAccess(final String user, final Permissions permissions)
			throws C8DBException {
		executor.execute(updateUserDefaultCollectionAccessRequest(user, permissions), Void.class);
	}

	@Override
	public Response execute(final Request request) throws C8DBException {
		return executor.execute(request, new ResponseDeserializer<Response>() {
			@Override
			public Response deserialize(final Response response) throws VPackException {
				return response;
			}
		});
	}

	@Override
	public Response execute(final Request request, final HostHandle hostHandle) throws C8DBException {
		return executor.execute(request, new ResponseDeserializer<Response>() {
			@Override
			public Response deserialize(final Response response) throws VPackException {
				return response;
			}
		}, hostHandle);
	}

	@Override
	public LogEntity getLogs(final LogOptions options) throws C8DBException {
		return executor.execute(getLogsRequest(options), LogEntity.class);
	}

	@Override
	public LogLevelEntity getLogLevel() throws C8DBException {
		return executor.execute(getLogLevelRequest(), LogLevelEntity.class);
	}

	@Override
	public LogLevelEntity setLogLevel(final LogLevelEntity entity) throws C8DBException {
		return executor.execute(setLogLevelRequest(entity), LogLevelEntity.class);
	}

	@Override
	public C8DBImpl _setCursorInitializer(final C8CursorInitializer cursorInitializer) {
		this.cursorInitializer = cursorInitializer;
		return this;
	}

}
