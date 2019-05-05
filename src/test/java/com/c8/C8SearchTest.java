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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.c8.C8DBException;
import com.c8.C8Search;
import com.c8.C8View;
import com.c8.C8DB.Builder;
import com.c8.entity.ServerRole;
import com.c8.entity.ViewEntity;
import com.c8.entity.ViewType;
import com.c8.entity.c8search.C8SearchPropertiesEntity;
import com.c8.entity.c8search.CollectionLink;
import com.c8.entity.c8search.ConsolidationPolicy;
import com.c8.entity.c8search.ConsolidationType;
import com.c8.entity.c8search.FieldLink;
import com.c8.entity.c8search.StoreValuesType;
import com.c8.model.c8search.C8SearchCreateOptions;
import com.c8.model.c8search.C8SearchPropertiesOptions;

/**
 * 
 *
 */
@RunWith(Parameterized.class)
public class C8SearchTest extends BaseTest {

	private static final String VIEW_NAME = "view_test";

	public C8SearchTest(final Builder builder) {
		super(builder);
	}

	@After
	public void teardown() {
		try {
			db.view(VIEW_NAME).drop();
		} catch (final C8DBException e) {
		}
	}

	@Test
	public void exists() {
		if (!requireVersion(3, 4)) {
			return;
		}
		db.createC8Search(VIEW_NAME, new C8SearchCreateOptions());
		assertThat(db.c8Search(VIEW_NAME).exists(), is(true));
	}

	@Test
	public void getInfo() {
		if (!requireVersion(3, 4)) {
			return;
		}
		db.createC8Search(VIEW_NAME, new C8SearchCreateOptions());
		final ViewEntity info = db.c8Search(VIEW_NAME).getInfo();
		assertThat(info, is(not(nullValue())));
		assertThat(info.getId(), is(not(nullValue())));
		assertThat(info.getName(), is(VIEW_NAME));
		assertThat(info.getType(), is(ViewType.c8_SEARCH));
	}

	@Test
	public void drop() {
		if (!requireVersion(3, 4)) {
			return;
		}
		db.createC8Search(VIEW_NAME, new C8SearchCreateOptions());
		final C8View view = db.c8Search(VIEW_NAME);
		view.drop();
		assertThat(view.exists(), is(false));
	}

	@Test
	public void rename() {
		if (c8DB.getRole() != ServerRole.SINGLE) {
			return;
		}
		if (!requireVersion(3, 4)) {
			return;
		}
		final String name = VIEW_NAME + "_new";
		db.createC8Search(name, new C8SearchCreateOptions());
		db.c8Search(name).rename(VIEW_NAME);
		assertThat(db.c8Search(name).exists(), is(false));
		assertThat(db.c8Search(VIEW_NAME).exists(), is(true));
	}

	@Test
	public void create() {
		if (!requireVersion(3, 4)) {
			return;
		}
		final ViewEntity info = db.c8Search(VIEW_NAME).create();
		assertThat(info, is(not(nullValue())));
		assertThat(info.getId(), is(not(nullValue())));
		assertThat(info.getName(), is(VIEW_NAME));
		assertThat(info.getType(), is(ViewType.c8_SEARCH));
		assertThat(db.c8Search(VIEW_NAME).exists(), is(true));
	}

	@Test
	public void createWithOptions() {
		if (!requireVersion(3, 4)) {
			return;
		}
		final C8SearchCreateOptions options = new C8SearchCreateOptions();
		final ViewEntity info = db.c8Search(VIEW_NAME).create(options);
		assertThat(info, is(not(nullValue())));
		assertThat(info.getId(), is(not(nullValue())));
		assertThat(info.getName(), is(VIEW_NAME));
		assertThat(info.getType(), is(ViewType.c8_SEARCH));
		assertThat(db.c8Search(VIEW_NAME).exists(), is(true));
	}

	@Test
	public void getProperties() {
		if (!requireVersion(3, 4)) {
			return;
		}
		final C8Search view = db.c8Search(VIEW_NAME);
		view.create(new C8SearchCreateOptions());
		final C8SearchPropertiesEntity properties = view.getProperties();
		assertThat(properties, is(not(nullValue())));
		assertThat(properties.getId(), is(not(nullValue())));
		assertThat(properties.getName(), is(VIEW_NAME));
		assertThat(properties.getType(), is(ViewType.c8_SEARCH));
		assertThat(properties.getConsolidationIntervalMsec(), is(not(nullValue())));
		assertThat(properties.getCleanupIntervalStep(), is(not(nullValue())));
		final ConsolidationPolicy consolidate = properties.getConsolidationPolicy();
		assertThat(consolidate, is(is(not(nullValue()))));
		final Collection<CollectionLink> links = properties.getLinks();
		assertThat(links.isEmpty(), is(true));
	}

	@Test
	public void updateProperties() {
		if (!requireVersion(3, 4)) {
			return;
		}
		db.createCollection("view_update_prop_test_collection");
		final C8Search view = db.c8Search(VIEW_NAME);
		view.create(new C8SearchCreateOptions());
		final C8SearchPropertiesOptions options = new C8SearchPropertiesOptions();
		options.cleanupIntervalStep(15L);
		options.consolidationIntervalMsec(65000L);
		options.consolidationPolicy(ConsolidationPolicy.of(ConsolidationType.BYTES_ACCUM).threshold(1.));
		options.link(
			CollectionLink.on("view_update_prop_test_collection").fields(FieldLink.on("value").analyzers("identity")
					.trackListPositions(true).includeAllFields(true).storeValues(StoreValuesType.ID)));
		final C8SearchPropertiesEntity properties = view.updateProperties(options);
		assertThat(properties, is(not(nullValue())));
		assertThat(properties.getCleanupIntervalStep(), is(15L));
		assertThat(properties.getConsolidationIntervalMsec(), is(65000L));
		final ConsolidationPolicy consolidate = properties.getConsolidationPolicy();
		assertThat(consolidate, is(not(nullValue())));
		assertThat(consolidate.getType(), is(ConsolidationType.BYTES_ACCUM));
		assertThat(consolidate.getThreshold(), is(1.));
		assertThat(properties.getLinks().size(), is(1));
		final CollectionLink link = properties.getLinks().iterator().next();
		assertThat(link.getName(), is("view_update_prop_test_collection"));
		assertThat(link.getFields().size(), is(1));
		final FieldLink next = link.getFields().iterator().next();
		assertThat(next.getName(), is("value"));
		assertThat(next.getIncludeAllFields(), is(true));
		assertThat(next.getTrackListPositions(), is(true));
		assertThat(next.getStoreValues(), is(StoreValuesType.ID));
	}

	@Test
	public void replaceProperties() {
		if (!requireVersion(3, 4)) {
			return;
		}
		db.createCollection("view_replace_prop_test_collection");
		final C8Search view = db.c8Search(VIEW_NAME);
		view.create(new C8SearchCreateOptions());
		final C8SearchPropertiesOptions options = new C8SearchPropertiesOptions();
		options.link(
			CollectionLink.on("view_replace_prop_test_collection").fields(FieldLink.on("value").analyzers("identity")));
		final C8SearchPropertiesEntity properties = view.replaceProperties(options);
		assertThat(properties, is(not(nullValue())));
		assertThat(properties.getLinks().size(), is(1));
		final CollectionLink link = properties.getLinks().iterator().next();
		assertThat(link.getName(), is("view_replace_prop_test_collection"));
		assertThat(link.getFields().size(), is(1));
		assertThat(link.getFields().iterator().next().getName(), is("value"));
	}

}
