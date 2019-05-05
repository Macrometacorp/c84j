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

import com.c8.model.c8search.C8SearchPropertiesOptions;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;

/**
 * 
 *
 */
public class InternalC8Search<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
		extends InternalC8View<A, D, E> {

	protected InternalC8Search(final D db, final String name) {
		super(db, name);
	}

	protected Request getPropertiesRequest() {
		return request(db.name(), RequestType.GET, PATH_API_VIEW, name, "properties");
	}

	protected Request replacePropertiesRequest(final C8SearchPropertiesOptions options) {
		final Request request = request(db.name(), RequestType.PUT, PATH_API_VIEW, name, "properties");
		request.setBody(util().serialize(options != null ? options : new C8SearchPropertiesOptions()));
		return request;
	}

	protected Request updatePropertiesRequest(final C8SearchPropertiesOptions options) {
		final Request request = request(db.name(), RequestType.PATCH, PATH_API_VIEW, name, "properties");
		request.setBody(util().serialize(options != null ? options : new C8SearchPropertiesOptions()));
		return request;
	}

}
