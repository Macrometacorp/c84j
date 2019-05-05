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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;

/**
 * 
 *
 */
public abstract class InternalC8Route<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
		extends C8Executeable<E> {

	protected final D db;
	protected final String path;

	protected final Map<String, String> queryParam;
	protected final Map<String, String> headerParam;
	protected Object body;

	protected InternalC8Route(final D db, final String path, final Map<String, String> headerParam) {
		super(db.executor, db.util, db.context);
		this.db = db;
		this.path = path;
		this.queryParam = new HashMap<String, String>();
		this.headerParam = new HashMap<String, String>();
		this.headerParam.putAll(headerParam);
	}

	public void _withHeader(final String key, final Object value) {
		if (value != null) {
			headerParam.put(key, value.toString());
		}
	}

	public void _withQueryParam(final String key, final Object value) {
		if (value != null) {
			queryParam.put(key, value.toString());
		}
	}

	public void _withBody(final Object body) {
		this.body = body;
	}

	protected Request createRequest(final RequestType requestType) {
		final Request request = request(db.name(), requestType, path);
		for (final Entry<String, String> param : headerParam.entrySet()) {
			request.putHeaderParam(param.getKey(), param.getValue());
		}
		for (final Entry<String, String> param : queryParam.entrySet()) {
			request.putQueryParam(param.getKey(), param.getValue());
		}
		if (body != null) {
			request.setBody(util().serialize(body));
		}
		return request;
	}
}
