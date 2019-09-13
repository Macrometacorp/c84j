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

package com.c8db;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import com.c8db.C8DB.Builder;
import com.c8db.entity.CollectionPropertiesEntity;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.GraphEntity;
import com.c8db.entity.ServerRole;
import com.c8db.model.GraphCreateOptions;

/**
 * 
 */
@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class C8GraphTest extends BaseTest {

    private static final String GRAPH_NAME = "dbCollectionTest";
    private static final String EDGE_COL_1 = "dbEdge1CollectionTest";
    private static final String EDGE_COL_2 = "dbEdge2CollectionTest";
    private static final String EDGE_COL_3 = "dbEdge3CollectionTest";
    private static final String VERTEX_COL_1 = "dbVertex1CollectionTest";
    private static final String VERTEX_COL_2 = "dbVertex2CollectionTest";
    private static final String VERTEX_COL_3 = "dbVertex3CollectionTest";
    private static final String VERTEX_COL_4 = "dbVertex4CollectionTest";
    private static final Integer REPLICATION_FACTOR = 1;
    private static final Integer NUMBER_OF_SHARDS = 1;

    public C8GraphTest(final Builder builder) {
        super(builder);
    }

    @Before
    public void setup() {
        try {
            db.graph(GRAPH_NAME).drop(true);
        } catch (final C8DBException e1) {
        }
        final Collection<EdgeDefinition> edgeDefinitions = new ArrayList<EdgeDefinition>();
        edgeDefinitions.add(new EdgeDefinition().collection(EDGE_COL_1).from(VERTEX_COL_1).to(VERTEX_COL_2));
        edgeDefinitions
                .add(new EdgeDefinition().collection(EDGE_COL_2).from(VERTEX_COL_2).to(VERTEX_COL_1, VERTEX_COL_3));
        final GraphCreateOptions options = new GraphCreateOptions();
        db.createGraph(GRAPH_NAME, edgeDefinitions, options);
    }

    @After
    public void teardown() {
        for (final String collection : new String[]{EDGE_COL_1, EDGE_COL_2, VERTEX_COL_1, VERTEX_COL_2, VERTEX_COL_3,
                VERTEX_COL_4}) {
            final C8Collection c = db.collection(collection);
            if (c.exists()) {
                c.truncate();
            }
        }
    }

    @Test
    public void exists() {
        assertThat(db.graph(GRAPH_NAME).exists(), is(true));
        assertThat(db.graph(GRAPH_NAME + "no").exists(), is(false));
    }

    @Test
    public void create() {
        try {
            final GraphEntity result = db.graph(GRAPH_NAME + "_1").create(null);
            assertThat(result, is(notNullValue()));
            assertThat(result.getName(), is(GRAPH_NAME + "_1"));
        } finally {
            db.graph(GRAPH_NAME + "_1").drop();
        }
    }

    @Test
    public void createWithNumberOfShards() {
        try {
            final Collection<EdgeDefinition> edgeDefinitions = new ArrayList<EdgeDefinition>();
            final GraphEntity graph = db.createGraph(GRAPH_NAME + "_1", edgeDefinitions, new GraphCreateOptions().numberOfShards(1));
            assertThat(graph, is(notNullValue()));
            assertThat(graph.getName(), is(GRAPH_NAME + "_1"));
            assertThat(graph.getNumberOfShards(), is(1));
        } finally {
            db.graph(GRAPH_NAME + "_1").drop();
        }
    }

    @Test
    public void getGraphs() {
        final Collection<GraphEntity> graphs = db.getGraphs();
        assertThat(graphs, is(notNullValue()));
        assertThat(graphs.size(), is(1));
        assertThat(graphs.iterator().next().getName(), is(GRAPH_NAME));
    }

    @Test
    public void getInfo() {
        final GraphEntity info = db.graph(GRAPH_NAME).getInfo();
        assertThat(info, is(notNullValue()));
        assertThat(info.getName(), is(GRAPH_NAME));
        assertThat(info.getEdgeDefinitions().size(), is(2));
        final Iterator<EdgeDefinition> iterator = info.getEdgeDefinitions().iterator();
        final EdgeDefinition e1 = iterator.next();
        assertThat(e1.getCollection(), is(EDGE_COL_1));
        assertThat(e1.getFrom(), hasItem(VERTEX_COL_1));
        assertThat(e1.getTo(), hasItem(VERTEX_COL_2));
        final EdgeDefinition e2 = iterator.next();
        assertThat(e2.getCollection(), is(EDGE_COL_2));
        assertThat(e2.getFrom(), hasItem(VERTEX_COL_2));
        assertThat(e2.getTo(), hasItems(VERTEX_COL_1, VERTEX_COL_3));
        assertThat(info.getOrphanCollections(), is(empty()));

        if (c8DB.getRole() != ServerRole.SINGLE) {
            for (final String collection : new String[]{EDGE_COL_1, EDGE_COL_2, VERTEX_COL_1, VERTEX_COL_2}) {
                final CollectionPropertiesEntity properties = db.collection(collection).getProperties();
                assertThat(properties.getNumberOfShards(), is(NUMBER_OF_SHARDS));
            }
        }
    }

    @Test
    public void getVertexCollections() {
        final Collection<String> vertexCollections = db.graph(GRAPH_NAME).getVertexCollections();
        assertThat(vertexCollections, is(notNullValue()));
        assertThat(vertexCollections.size(), is(3));
        assertThat(vertexCollections, hasItems(VERTEX_COL_1, VERTEX_COL_2, VERTEX_COL_3));
    }

    @Test
    public void addVertexCollection() {
        final GraphEntity graph = db.graph(GRAPH_NAME).addVertexCollection(VERTEX_COL_4);
        assertThat(graph, is(notNullValue()));
        final Collection<String> vertexCollections = db.graph(GRAPH_NAME).getVertexCollections();
        assertThat(vertexCollections, hasItems(VERTEX_COL_1, VERTEX_COL_2, VERTEX_COL_3, VERTEX_COL_4));
        //TODO: investigate why this is here!?!?
        setup();
    }

    @Test
    public void getEdgeCollections() {
        final Collection<String> edgeCollections = db.graph(GRAPH_NAME).getEdgeDefinitions();
        assertThat(edgeCollections, is(notNullValue()));
        assertThat(edgeCollections.size(), is(2));
        assertThat(edgeCollections, hasItems(EDGE_COL_1, EDGE_COL_2));
    }

    @Test
    public void addEdgeDefinition() {
        final GraphEntity graph = db.graph(GRAPH_NAME)
                .addEdgeDefinition(new EdgeDefinition().collection(EDGE_COL_3).from(VERTEX_COL_1).to(VERTEX_COL_2));
        assertThat(graph, is(notNullValue()));
        final Collection<EdgeDefinition> edgeDefinitions = graph.getEdgeDefinitions();
        assertThat(edgeDefinitions.size(), is(3));
        int count = 0;
        for (final EdgeDefinition e : edgeDefinitions) {
            if (e.getCollection().equals(EDGE_COL_3)) {
                count++;
            }
        }
        assertThat(count, is(1));
        for (final EdgeDefinition e : edgeDefinitions) {
            if (e.getCollection().equals(EDGE_COL_3)) {
                assertThat(e.getFrom(), hasItem(VERTEX_COL_1));
                assertThat(e.getTo(), hasItem(VERTEX_COL_2));
            }
        }
        if (c8DB.getRole() != ServerRole.SINGLE) {
            final CollectionPropertiesEntity properties = db.collection(EDGE_COL_3).getProperties();
            assertThat(properties.getReplicationFactor(), is(REPLICATION_FACTOR));
            assertThat(properties.getNumberOfShards(), is(NUMBER_OF_SHARDS));
        }
        setup();
    }

    @Test
    public void replaceEdgeDefinition() {
        final GraphEntity graph = db.graph(GRAPH_NAME)
                .replaceEdgeDefinition(new EdgeDefinition().collection(EDGE_COL_1).from(VERTEX_COL_3).to(VERTEX_COL_4));
        final Collection<EdgeDefinition> edgeDefinitions = graph.getEdgeDefinitions();
        assertThat(edgeDefinitions.size(), is(2));
        int count = 0;
        for (final EdgeDefinition e : edgeDefinitions) {
            if (e.getCollection().equals(EDGE_COL_1)) {
                count++;
            }
        }
        assertThat(count, is(1));
        for (final EdgeDefinition e : edgeDefinitions) {
            if (e.getCollection().equals(EDGE_COL_1)) {
                assertThat(e.getFrom(), hasItem(VERTEX_COL_3));
                assertThat(e.getTo(), hasItem(VERTEX_COL_4));
            }
        }
        setup();
    }

    @Test
    public void removeEdgeDefinition() {
        final GraphEntity graph = db.graph(GRAPH_NAME).removeEdgeDefinition(EDGE_COL_1);
        final Collection<EdgeDefinition> edgeDefinitions = graph.getEdgeDefinitions();
        assertThat(edgeDefinitions.size(), is(1));
        assertThat(edgeDefinitions.iterator().next().getCollection(), is(EDGE_COL_2));
        //TODO: investigate why this is here!?!?
        setup();
    }

    @Test
    public void drop() {
        final String edgeCollection = "edge_drop";
        final String vertexCollection = "vertex_drop";
        final String graph = GRAPH_NAME + "_drop";
        final GraphEntity result = db.graph(graph).create(Collections
                .singleton(new EdgeDefinition().collection(edgeCollection).from(vertexCollection).to(vertexCollection)));
        assertThat(result, is(notNullValue()));
        db.graph(graph).drop();
        assertThat(db.collection(edgeCollection).exists(), is(true));
        assertThat(db.collection(vertexCollection).exists(), is(true));
    }

    @Test
    public void dropPlusDropCollections() {
        final String edgeCollection = "edge_dropC";
        final String vertexCollection = "vertex_dropC";
        final String graph = GRAPH_NAME + "_dropC";
        final GraphEntity result = db.graph(graph).create(Collections
                .singleton(new EdgeDefinition().collection(edgeCollection).from(vertexCollection).to(vertexCollection)));
        assertThat(result, is(notNullValue()));
        db.graph(graph).drop(true);
        assertThat(db.collection(edgeCollection).exists(), is(false));
        assertThat(db.collection(vertexCollection).exists(), is(false));
    }

    @AfterClass
    public static void shutdown() {
        db.drop();
    }
}
