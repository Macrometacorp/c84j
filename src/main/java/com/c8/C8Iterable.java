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

package com.c8;

import java.util.Collection;

/**
 * 
 *
 */
public interface C8Iterable<T> extends Iterable<T> {

	@Override
	C8Iterator<T> iterator();

	/**
	 * Performs the given action for each element of the {@code C8Iterable}
	 * 
	 * @param action
	 *            a action to perform on the elements
	 */
	void foreach(Consumer<? super T> action);

	/**
	 * Returns a {@code C8Iterable} consisting of the results of applying the given function to the elements of this
	 * {@code C8Iterable}.
	 * 
	 * @param mapper
	 *            a function to apply to each element
	 * @return the new {@code C8Iterable}
	 */
	<R> C8Iterable<R> map(Function<? super T, ? extends R> mapper);

	/**
	 * Returns a {@code C8Iterable} consisting of the elements of this {@code C8Iterable} that match the given
	 * predicate.
	 * 
	 * @param predicate
	 *            a predicate to apply to each element to determine if it should be included
	 * @return the new {@code C8Iterable}
	 */
	C8Iterable<T> filter(Predicate<? super T> predicate);

	/**
	 * Returns the first element or {@code null} if no element exists.
	 * 
	 * @return first element or {@code null}
	 */
	T first();

	/**
	 * Returns the count of elements of this {@code C8Iterable}.
	 * 
	 * @return the count of elements
	 */
	long count();

	/**
	 * Returns whether any elements of this {@code C8Iterable} match the provided predicate.
	 * 
	 * @param predicate
	 *            a predicate to apply to elements of this {@code C8Iterable}
	 * @return {@code true} if any elements of the {@code C8Iterable} match the provided predicate, otherwise
	 *         {@code false}
	 */
	boolean anyMatch(Predicate<? super T> predicate);

	/**
	 * Returns whether all elements of this {@code C8Iterable} match the provided predicate.
	 * 
	 * @param predicate
	 *            a predicate to apply to elements of this {@code C8Iterable}
	 * @return {@code true} if all elements of the {@code C8Iterable} match the provided predicate, otherwise
	 *         {@code false}
	 */
	boolean allMatch(Predicate<? super T> predicate);

	/**
	 * Returns whether no elements of this {@code C8Iterable} match the provided predicate.
	 * 
	 * @param predicate
	 *            a predicate to apply to elements of this {@code C8Iterable}
	 * @return {@code true} if no elements of the {@code C8Iterable} match the provided predicate, otherwise
	 *         {@code false}
	 */
	boolean noneMatch(Predicate<? super T> predicate);

	/**
	 * Iterates over all elements of this {@code C8Iterable} and adds each to the given target.
	 * 
	 * @param target
	 *            the collection to insert into
	 * @return the filled target
	 */
	<R extends Collection<? super T>> R collectInto(R target);

}
