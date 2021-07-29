/*
 * Copyright (c) 2021 Macrometa Corp All rights reserved
 */

package com.c8db.example.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.c8db.C8DB;
import com.c8db.C8DBException;
import com.c8db.C8Database;
import com.c8db.entity.EdgeDefinition;
import com.c8db.entity.EdgeEntity;
import com.c8db.entity.VertexEntity;
import com.c8db.internal.C8Defaults;

/**
 *
 */
public abstract class BaseGraphTest {

    protected static final String TEST_DB = "java_driver_graph_test_db";
    protected static C8DB arangoDB;
    protected static C8Database db;
    protected static final String GRAPH_NAME = "traversalGraph";
    protected static final String EDGE_COLLECTION_NAME = "edges";
    protected static final String VERTEXT_COLLECTION_NAME = "circles";

    @BeforeClass
    public static void init() {
        if (arangoDB == null) {
            arangoDB = new C8DB.Builder().build();
        }
        try {
            arangoDB.db(C8Defaults.DEFAULT_TENANT, TEST_DB).drop();
        } catch (final C8DBException e) {
        }
        arangoDB.createGeoFabric(C8Defaults.DEFAULT_TENANT, TEST_DB, "", C8Defaults.DEFAULT_DC_LIST, TEST_DB);
        BaseGraphTest.db = arangoDB.db(C8Defaults.DEFAULT_TENANT, TEST_DB);

        final Collection<EdgeDefinition> edgeDefinitions = new ArrayList<EdgeDefinition>();
        final EdgeDefinition edgeDefinition = new EdgeDefinition().collection(EDGE_COLLECTION_NAME)
                .from(VERTEXT_COLLECTION_NAME).to(VERTEXT_COLLECTION_NAME);
        edgeDefinitions.add(edgeDefinition);
        try {
            db.createGraph(GRAPH_NAME, edgeDefinitions, null);
            addExampleElements();
        } catch (final C8DBException ex) {

        }
    }

    @AfterClass
    public static void shutdown() {
        arangoDB.db(C8Defaults.DEFAULT_TENANT, TEST_DB).drop();
        arangoDB.shutdown();
        arangoDB = null;
    }

    private static void addExampleElements() throws C8DBException {

        // Add circle circles
        final VertexEntity vA = createVertex(new Circle("A", "1"));
        final VertexEntity vB = createVertex(new Circle("B", "2"));
        final VertexEntity vC = createVertex(new Circle("C", "3"));
        final VertexEntity vD = createVertex(new Circle("D", "4"));
        final VertexEntity vE = createVertex(new Circle("E", "5"));
        final VertexEntity vF = createVertex(new Circle("F", "6"));
        final VertexEntity vG = createVertex(new Circle("G", "7"));
        final VertexEntity vH = createVertex(new Circle("H", "8"));
        final VertexEntity vI = createVertex(new Circle("I", "9"));
        final VertexEntity vJ = createVertex(new Circle("J", "10"));
        final VertexEntity vK = createVertex(new Circle("K", "11"));

        // Add relevant edges - left branch:
        saveEdge(new CircleEdge(vA.getId(), vB.getId(), false, true, "left_bar"));
        saveEdge(new CircleEdge(vB.getId(), vC.getId(), false, true, "left_blarg"));
        saveEdge(new CircleEdge(vC.getId(), vD.getId(), false, true, "left_blorg"));
        saveEdge(new CircleEdge(vB.getId(), vE.getId(), false, true, "left_blub"));
        saveEdge(new CircleEdge(vE.getId(), vF.getId(), false, true, "left_schubi"));

        // Add relevant edges - right branch:
        saveEdge(new CircleEdge(vA.getId(), vG.getId(), false, true, "right_foo"));
        saveEdge(new CircleEdge(vG.getId(), vH.getId(), false, true, "right_blob"));
        saveEdge(new CircleEdge(vH.getId(), vI.getId(), false, true, "right_blub"));
        saveEdge(new CircleEdge(vG.getId(), vJ.getId(), false, true, "right_zip"));
        saveEdge(new CircleEdge(vJ.getId(), vK.getId(), false, true, "right_zup"));
    }

    private static EdgeEntity saveEdge(final CircleEdge edge) throws C8DBException {
        return db.graph(GRAPH_NAME).edgeCollection(EDGE_COLLECTION_NAME).insertEdge(edge);
    }

    private static VertexEntity createVertex(final Circle vertex) throws C8DBException {
        return db.graph(GRAPH_NAME).vertexCollection(VERTEXT_COLLECTION_NAME).insertVertex(vertex);
    }

}
