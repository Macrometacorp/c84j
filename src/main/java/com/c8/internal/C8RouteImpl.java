/*
 * DISCLAIMER
 *
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
 *
 */

package com.c8.internal;

import java.util.Map;

import com.arangodb.velocypack.exception.VPackException;
import com.c8.C8DBException;
import com.c8.C8Route;
import com.c8.internal.C8Executor.ResponseDeserializer;
import com.c8.velocystream.RequestType;
import com.c8.velocystream.Response;

/**
 * 
 *
 */
public class C8RouteImpl extends InternalC8Route<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
		implements C8Route {

	protected C8RouteImpl(final C8DatabaseImpl db, final String path, final Map<String, String> headerParam) {
		super(db, path, headerParam);
	}

	@Override
	public C8Route route(final String... path) {
		return new C8RouteImpl(db, createPath(this.path, createPath(path)), headerParam);
	}

	@Override
	public C8Route withHeader(final String key, final Object value) {
		_withHeader(key, value);
		return this;
	}

	@Override
	public C8Route withQueryParam(final String key, final Object value) {
		_withQueryParam(key, value);
		return this;
	}

	@Override
	public C8Route withBody(final Object body) {
		_withBody(body);
		return this;
	}

	private Response request(final RequestType requestType) {
		return executor.execute(createRequest(requestType), new ResponseDeserializer<Response>() {
			@Override
			public Response deserialize(final Response response) throws VPackException {
				return response;
			}
		});
	}

	@Override
	public Response delete() throws C8DBException {
		return request(RequestType.DELETE);
	}

	@Override
	public Response get() throws C8DBException {
		return request(RequestType.GET);
	}

	@Override
	public Response head() throws C8DBException {
		return request(RequestType.HEAD);
	}

	@Override
	public Response patch() throws C8DBException {
		return request(RequestType.PATCH);
	}

	@Override
	public Response post() throws C8DBException {
		return request(RequestType.POST);
	}

	@Override
	public Response put() throws C8DBException {
		return request(RequestType.PUT);
	}

}
