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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.c8db.util.MapBuilder;

/**
 *
 */
public class MapBuilderTest {

	@Test
	public void build() {
		final Map<String, Object> map = new MapBuilder().put("foo", "bar").get();
		assertThat(map.size(), is(1));
		assertThat(map.get("foo"), is(notNullValue()));
		assertThat(map.get("foo").toString(), is("bar"));
	}
}
