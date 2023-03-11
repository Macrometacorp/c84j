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

package com.c8db.internal.cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.c8db.C8Cursor;
import com.c8db.C8Iterator;
import com.c8db.Consumer;
import com.c8db.entity.CursorEntity;
import com.c8db.entity.CursorEntity.Extras;
import com.c8db.entity.CursorEntity.Stats;
import com.c8db.entity.CursorEntity.Warning;
import com.c8db.internal.C8CursorExecute;
import com.c8db.internal.InternalC8Database;

/**
 *
 */
public class C8CursorImpl<T> extends AbstractC8Iterable<T> implements C8Cursor<T> {

	private final Class<T> type;
	protected final C8CursorIterator<T> iterator;
	private final String id;
	private final C8CursorExecute execute;

	public C8CursorImpl(final InternalC8Database<?, ?> db, final C8CursorExecute execute,
		final Class<T> type, final CursorEntity<T> result) {
		super();
		this.execute = execute;
		this.type = type;
		iterator = createIterator(this, db, execute, result);
		id = result.getId();
	}

	protected C8CursorIterator<T> createIterator(
		final C8Cursor<T> cursor,
		final InternalC8Database<?, ?> db,
		final C8CursorExecute execute,
		final CursorEntity<T> result) {
		return new C8CursorIterator<T>(cursor, execute, db, result);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public Integer getCount() {
		return iterator.getResult().getCount();
	}

	@Override
	public Stats getStats() {
		final Extras extra = iterator.getResult().getExtra();
		return extra != null ? extra.getStats() : null;
	}

	@Override
	public Collection<Warning> getWarnings() {
		final Extras extra = iterator.getResult().getExtra();
		return extra != null ? extra.getWarnings() : null;
	}

	@Override
	public boolean isCached() {
		final Boolean cached = iterator.getResult().getCached();
		return Boolean.TRUE == cached;
	}

	@Override
	public void close() {
		if (id != null && hasNext()) {
			execute.close(id, iterator.getResult().getMeta());
		}
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return iterator.next();
	}

	@Override
	public List<T> asListRemaining() {
		final List<T> remaining = new ArrayList<T>();
		while (hasNext()) {
			remaining.add(next());
		}
		return remaining;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public C8Iterator<T> iterator() {
		return iterator;
	}

	@Override
	public void foreach(final Consumer<? super T> action) {
		while (hasNext()) {
			action.accept(next());
		}
	}

}
