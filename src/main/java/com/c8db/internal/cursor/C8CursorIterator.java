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

import java.util.NoSuchElementException;

import com.arangodb.velocypack.VPackSlice;
import com.c8db.C8Cursor;
import com.c8db.C8Iterator;
import com.c8db.entity.CursorEntity;
import com.c8db.internal.C8CursorExecute;
import com.c8db.internal.InternalC8Database;
import com.c8db.internal.util.C8SerializationFactory.Serializer;

/**
 * @param <T>
 *
 */
public class C8CursorIterator<T> implements C8Iterator<T> {

    private CursorEntity<T> result;
    private int pos;

    private final C8Cursor<T> cursor;
    private final InternalC8Database<?, ?> db;
    private final C8CursorExecute execute;

    protected C8CursorIterator(final C8Cursor<T> cursor, final C8CursorExecute execute,
            final InternalC8Database<?, ?> db, final CursorEntity<T> result) {
        super();
        this.cursor = cursor;
        this.execute = execute;
        this.db = db;
        this.result = result;
        pos = 0;
    }

    public CursorEntity<T> getResult() {
        return result;
    }

    @Override
    public boolean hasNext() {
        return pos < result.getResult().size() || (result.getHasMore() != null && result.getHasMore());
    }

    @Override
    public T next() {
        if (pos >= result.getResult().size() && result.getHasMore()) {
            result = execute.next(cursor.getId(), result.getMeta());
            pos = 0;
        }
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return deserialize(result.getResult().get(pos++), cursor.getType());
    }

    protected <R> R deserialize(final VPackSlice result, final Class<R> type) {
        return db.util(Serializer.CUSTOM).deserialize(result, type);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
