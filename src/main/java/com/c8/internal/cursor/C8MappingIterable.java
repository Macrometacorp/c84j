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

package com.c8.internal.cursor;

import com.c8.C8Iterable;
import com.c8.C8Iterator;
import com.c8.Consumer;
import com.c8.Function;

/**
 * 
 *
 */
public class C8MappingIterable<R, T> extends AbstractC8Iterable<T> implements C8Iterable<T> {

	private final C8Iterable<R> iterable;
	private final Function<? super R, ? extends T> mapper;

	protected C8MappingIterable(final C8Iterable<R> iterable, final Function<? super R, ? extends T> mapper) {
		super();
		this.iterable = iterable;
		this.mapper = mapper;
	}

	@Override
	public C8Iterator<T> iterator() {
		return new C8MappingIterator<R, T>(iterable.iterator(), mapper);
	}

	@Override
	public void foreach(final Consumer<? super T> action) {
		for (final R t : iterable) {
			action.accept(mapper.apply(t));
		}
	}

}
