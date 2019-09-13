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

package com.c8db.util;

import com.c8db.C8Cursor;
import com.c8db.entity.CursorEntity;
import com.c8db.internal.C8CursorExecute;
import com.c8db.internal.InternalC8Database;

/**
 *
 */
public interface C8CursorInitializer {

    <T> C8Cursor<T> createInstance(final InternalC8Database<?, ?> db, final C8CursorExecute execute,
            final Class<T> type, final CursorEntity result);

}
