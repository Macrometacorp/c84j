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
import com.c8.C8View;
import com.c8.entity.ViewEntity;

/**
 * 
 *
 */
public class C8ViewImpl extends InternalC8View<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
		implements C8View {

	protected C8ViewImpl(final C8DatabaseImpl db, final String name) {
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

}
