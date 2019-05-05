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

import com.c8.C8DBException;
import com.c8.C8Search;
import com.c8.entity.ViewEntity;
import com.c8.entity.c8search.C8SearchPropertiesEntity;
import com.c8.model.c8search.C8SearchCreateOptions;
import com.c8.model.c8search.C8SearchPropertiesOptions;

/**
 * 
 *
 */
public class C8SearchImpl extends InternalC8Search<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
		implements C8Search {

	protected C8SearchImpl(final C8DatabaseImpl db, final String name) {
		super(db, name);
	}

	@Override
	public boolean exists() throws C8DBException {
		try {
			getInfo();
			return true;
		} catch (final C8DBException e) {
			if (C8Errors.ERROR_c8_DATA_SOURCE_NOT_FOUND.equals(e.getErrorNum())) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public void drop() throws C8DBException {
		executor.execute(dropRequest(), Void.class);
	}

	@Override
	public synchronized ViewEntity rename(final String newName) throws C8DBException {
		final ViewEntity result = executor.execute(renameRequest(newName), ViewEntity.class);
		name = result.getName();
		return result;
	}

	@Override
	public ViewEntity getInfo() throws C8DBException {
		return executor.execute(getInfoRequest(), ViewEntity.class);
	}

	@Override
	public ViewEntity create() throws C8DBException {
		return create(new C8SearchCreateOptions());
	}

	@Override
	public ViewEntity create(final C8SearchCreateOptions options) throws C8DBException {
		return db().createC8Search(name(), options);
	}

	@Override
	public C8SearchPropertiesEntity getProperties() throws C8DBException {
		return executor.execute(getPropertiesRequest(), C8SearchPropertiesEntity.class);
	}

	@Override
	public C8SearchPropertiesEntity updateProperties(final C8SearchPropertiesOptions options)
			throws C8DBException {
		return executor.execute(updatePropertiesRequest(options), C8SearchPropertiesEntity.class);
	}

	@Override
	public C8SearchPropertiesEntity replaceProperties(final C8SearchPropertiesOptions options)
			throws C8DBException {
		return executor.execute(replacePropertiesRequest(options), C8SearchPropertiesEntity.class);
	}

}
