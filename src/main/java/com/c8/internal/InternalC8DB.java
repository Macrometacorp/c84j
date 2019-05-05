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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import com.arangodb.velocypack.Type;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.c8.entity.LogLevelEntity;
import com.c8.entity.Permissions;
import com.c8.entity.ServerRole;
import com.c8.entity.UserEntity;
import com.c8.internal.C8Executor.ResponseDeserializer;
import com.c8.internal.util.C8SerializationFactory;
import com.c8.model.DBCreateOptions;
import com.c8.model.LogOptions;
import com.c8.model.OptionsBuilder;
import com.c8.model.UserAccessOptions;
import com.c8.model.UserCreateOptions;
import com.c8.model.UserUpdateOptions;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public abstract class InternalC8DB<E extends C8Executor> extends C8Executeable<E> {

	private static final String PATH_API_ADMIN_LOG = "/_admin/log";
	private static final String PATH_API_ADMIN_LOG_LEVEL = "/_admin/log/level";
	private static final String PATH_API_ROLE = "/_admin/server/role";
	protected static final String PATH_ENDPOINTS = "/_api/cluster/endpoints";
	private static final String PATH_API_USER = "/_api/user";

	protected InternalC8DB(final E executor, final C8SerializationFactory util, final C8Context context) {
		super(executor, util, context);
	}

	protected Request getRoleRequest() {
		return request(C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ROLE);
	}

	protected ResponseDeserializer<ServerRole> getRoleResponseDeserializer() {
		return new ResponseDeserializer<ServerRole>() {
			@Override
			public ServerRole deserialize(final Response response) throws VPackException {
				return util().deserialize(response.getBody().get("role"), ServerRole.class);
			}
		};
	}

	protected Request createDatabaseRequest(final String name) {
		final Request request = request(C8RequestParam.SYSTEM, RequestType.POST,
			InternalC8Database.PATH_API_DATABASE);
		request.setBody(util().serialize(OptionsBuilder.build(new DBCreateOptions(), name)));
		return request;
	}

	protected ResponseDeserializer<Boolean> createDatabaseResponseDeserializer() {
		return new ResponseDeserializer<Boolean>() {
			@Override
			public Boolean deserialize(final Response response) throws VPackException {
				return response.getBody().get(C8ResponseField.RESULT).getAsBoolean();
			}
		};
	}

	protected Request getDatabasesRequest(final String database) {
		return request(database, RequestType.GET, InternalC8Database.PATH_API_DATABASE);
	}

	protected ResponseDeserializer<Collection<String>> getDatabaseResponseDeserializer() {
		return new ResponseDeserializer<Collection<String>>() {
			@Override
			public Collection<String> deserialize(final Response response) throws VPackException {
				final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
				return util().deserialize(result, new Type<Collection<String>>() {
				}.getType());
			}
		};
	}

	protected Request getAccessibleDatabasesForRequest(final String database, final String user) {
		return request(database, RequestType.GET, PATH_API_USER, user, C8RequestParam.DATABASE);
	}

	protected ResponseDeserializer<Collection<String>> getAccessibleDatabasesForResponseDeserializer() {
		return new ResponseDeserializer<Collection<String>>() {
			@Override
			public Collection<String> deserialize(final Response response) throws VPackException {
				final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
				final Collection<String> dbs = new ArrayList<String>();
				for (final Iterator<Entry<String, VPackSlice>> iterator = result.objectIterator(); iterator
						.hasNext();) {
					dbs.add(iterator.next().getKey());
				}
				return dbs;
			}
		};
	}

	protected Request createUserRequest(
		final String database,
		final String user,
		final String passwd,
		final UserCreateOptions options) {
		final Request request;
		request = request(database, RequestType.POST, PATH_API_USER);
		request.setBody(
			util().serialize(OptionsBuilder.build(options != null ? options : new UserCreateOptions(), user, passwd)));
		return request;
	}

	protected Request deleteUserRequest(final String database, final String user) {
		return request(database, RequestType.DELETE, PATH_API_USER, user);
	}

	protected Request getUsersRequest(final String database) {
		return request(database, RequestType.GET, PATH_API_USER);
	}

	protected Request getUserRequest(final String database, final String user) {
		return request(database, RequestType.GET, PATH_API_USER, user);
	}

	protected ResponseDeserializer<Collection<UserEntity>> getUsersResponseDeserializer() {
		return new ResponseDeserializer<Collection<UserEntity>>() {
			@Override
			public Collection<UserEntity> deserialize(final Response response) throws VPackException {
				final VPackSlice result = response.getBody().get(C8ResponseField.RESULT);
				return util().deserialize(result, new Type<Collection<UserEntity>>() {
				}.getType());
			}
		};
	}

	protected Request updateUserRequest(final String database, final String user, final UserUpdateOptions options) {
		final Request request;
		request = request(database, RequestType.PATCH, PATH_API_USER, user);
		request.setBody(util().serialize(options != null ? options : new UserUpdateOptions()));
		return request;
	}

	protected Request replaceUserRequest(final String database, final String user, final UserUpdateOptions options) {
		final Request request;
		request = request(database, RequestType.PUT, PATH_API_USER, user);
		request.setBody(util().serialize(options != null ? options : new UserUpdateOptions()));
		return request;
	}

	protected Request updateUserDefaultDatabaseAccessRequest(final String user, final Permissions permissions) {
		return request(C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
			"*").setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
	}

	protected Request updateUserDefaultCollectionAccessRequest(final String user, final Permissions permissions) {
		return request(C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_USER, user, C8RequestParam.DATABASE,
			"*", "*").setBody(util().serialize(OptionsBuilder.build(new UserAccessOptions(), permissions)));
	}

	protected Request getLogsRequest(final LogOptions options) {
		final LogOptions params = options != null ? options : new LogOptions();
		return request(C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ADMIN_LOG)
				.putQueryParam(LogOptions.PROPERTY_UPTO, params.getUpto())
				.putQueryParam(LogOptions.PROPERTY_LEVEL, params.getLevel())
				.putQueryParam(LogOptions.PROPERTY_START, params.getStart())
				.putQueryParam(LogOptions.PROPERTY_SIZE, params.getSize())
				.putQueryParam(LogOptions.PROPERTY_OFFSET, params.getOffset())
				.putQueryParam(LogOptions.PROPERTY_SEARCH, params.getSearch())
				.putQueryParam(LogOptions.PROPERTY_SORT, params.getSort());
	}

	protected Request getLogLevelRequest() {
		return request(C8RequestParam.SYSTEM, RequestType.GET, PATH_API_ADMIN_LOG_LEVEL);
	}

	protected Request setLogLevelRequest(final LogLevelEntity entity) {
		return request(C8RequestParam.SYSTEM, RequestType.PUT, PATH_API_ADMIN_LOG_LEVEL)
				.setBody(util().serialize(entity));
	}

}
