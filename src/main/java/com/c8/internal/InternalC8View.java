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

import com.c8.model.OptionsBuilder;
import com.c8.model.ViewRenameOptions;
import com.c8.velocystream.Request;
import com.c8.velocystream.RequestType;

/**
 * 
 *
 */
public abstract class InternalC8View<A extends InternalC8DB<E>, D extends InternalC8Database<A, E>, E extends C8Executor>
		extends C8Executeable<E> {

	protected static final String PATH_API_VIEW = "/_api/view";

	protected final D db;
	protected volatile String name;

	protected InternalC8View(final D db, final String name) {
		super(db.executor, db.util, db.context);
		this.db = db;
		this.name = name;
	}

	public D db() {
		return db;
	}

	public String name() {
		return name;
	}

	protected Request dropRequest() {
		return request(db.name(), RequestType.DELETE, PATH_API_VIEW, name);
	}

	protected Request renameRequest(final String newName) {
		final Request request = request(db.name(), RequestType.PUT, PATH_API_VIEW, name, "rename");
		request.setBody(util().serialize(OptionsBuilder.build(new ViewRenameOptions(), newName)));
		return request;
	}

	protected Request getInfoRequest() {
		return request(db.name(), RequestType.GET, PATH_API_VIEW, name);
	}

}
