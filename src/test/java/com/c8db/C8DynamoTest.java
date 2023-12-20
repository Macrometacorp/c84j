/**
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */
package com.c8db;

import com.c8db.C8DB.Builder;
import com.c8db.entity.C8DynamoAttributeDefinition;
import com.c8db.entity.C8DynamoCreateTableEntity;
import com.c8db.entity.C8DynamoDescribeTableEntity;
import com.c8db.entity.C8DynamoGetItemEntity;
import com.c8db.entity.C8DynamoKeySchemaElement;
import com.c8db.entity.C8DynamoProjection;
import com.c8db.entity.C8DynamoPutItemEntity;
import com.c8db.entity.C8DynamoSecondaryIndex;
import com.c8db.model.C8DynamoAttributeType;
import com.c8db.model.C8DynamoCreateTableOptions;
import com.c8db.model.C8DynamoProjectionType;
import com.c8db.model.C8DynamoType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class C8DynamoTest extends BaseTest {

    private static String COLLECTION_NAME = "dynamo_test_1";

    private static C8DynamoAttributeDefinition userIdAttrbute = new C8DynamoAttributeDefinition("UserId",
            C8DynamoType.STRING);
    private static C8DynamoAttributeDefinition gameTitleAttrbute = new C8DynamoAttributeDefinition("GameTitle",
            C8DynamoType.STRING);
    private static C8DynamoAttributeDefinition topScoreAttrbute = new C8DynamoAttributeDefinition("TopScore",
            C8DynamoType.NUMBER);
    private static C8DynamoAttributeDefinition dateTimeAttrbute = new C8DynamoAttributeDefinition("DateTime",
            C8DynamoType.STRING);

    private static C8DynamoKeySchemaElement userIdKey = new C8DynamoKeySchemaElement("UserId",
            C8DynamoAttributeType.HASH);
    private static C8DynamoKeySchemaElement gameTitleKey = new C8DynamoKeySchemaElement("GameTitle",
            C8DynamoAttributeType.RANGE);

    private static C8DynamoSecondaryIndex globalSecondaryIndex = new C8DynamoSecondaryIndex("GameTitleIndex",
            Arrays.asList(
            new C8DynamoKeySchemaElement("GameTitle", C8DynamoAttributeType.HASH),
                                        new C8DynamoKeySchemaElement("TopScore", C8DynamoAttributeType.RANGE)
                                ), new C8DynamoProjection(C8DynamoProjectionType.ALL, null));

    public C8DynamoTest(final Builder builder) {
        super(builder);
    }

    @Test
    public void test1_create_dynamo_collection() {
        C8Dynamo dynamoColl = db.dynamo(COLLECTION_NAME);

        C8DynamoCreateTableEntity dynamoEntity = dynamoColl.createTable(
                C8DynamoCreateTableOptions.builder()
                        .attributeDefinitions(Arrays.asList(
                                userIdAttrbute,
                                gameTitleAttrbute,
                                topScoreAttrbute,
                                dateTimeAttrbute
                        ))
                        .keySchema(Arrays.asList(
                                userIdKey,
                                gameTitleKey))
                        .globalSecondaryIndexes(Arrays.asList(
                                globalSecondaryIndex
                        ))
                        .build());

        assertEquals(dynamoEntity.getTableDescription().getTableName(), COLLECTION_NAME);
    }

    @Test
    public void test2_describe_dynamo_collection() {
        C8Dynamo dynamoColl = db.dynamo(COLLECTION_NAME);

        C8DynamoDescribeTableEntity dynamoEntity = dynamoColl.describeTable();

        assertTrue(dynamoEntity.getTable().getAttributeDefinitions().containsAll(Arrays.asList(
                userIdAttrbute,
                gameTitleAttrbute,
                topScoreAttrbute,
                dateTimeAttrbute
        )));

        C8DynamoSecondaryIndex secondaryIndex = dynamoEntity.getTable().getGlobalSecondaryIndexes().iterator().next();
        assertTrue(dynamoEntity.getTable().getKeySchema().containsAll(Arrays.asList(
                userIdKey,
                gameTitleKey)));
        assertEquals(globalSecondaryIndex, secondaryIndex);
    }

    @Test
    public void test3_put_item() {
        C8Dynamo dynamoColl = db.dynamo(COLLECTION_NAME);

        Map<String, Object> item1 = new HashMap<>();
        item1.put("UserId", "user_1");
        item1.put("GameTitle", "GTA 5");
        item1.put("TopScore", 100);
        item1.put("DateTime", "10/10/2020 10:10:10");
        C8DynamoPutItemEntity putItemEntity = dynamoColl.putItem(item1);
    }

    @Test
    public void test4_get_item() {
        C8Dynamo dynamoColl = db.dynamo(COLLECTION_NAME);
        Map<String, Object> item1 = new HashMap<>();
        item1.put("UserId", "user_1");
        item1.put("GameTitle", "GTA 5");
        C8DynamoGetItemEntity getItemEntity = dynamoColl.getItem(item1);
        assertEquals("user_1", getItemEntity.getItem().get("UserId"));
        assertEquals("GTA 5", getItemEntity.getItem().get("GameTitle"));
        assertEquals("100", getItemEntity.getItem().get("TopScore"));
        assertEquals("10/10/2020 10:10:10", getItemEntity.getItem().get("DateTime"));
        assertEquals(1, getItemEntity.getConsumedCapacity().getCapacityUnits());
        assertEquals(COLLECTION_NAME, getItemEntity.getConsumedCapacity().getTableName());
    }

    @Test
    public void test5_drop() {
        db.dynamo(COLLECTION_NAME).deleteTable();
    }

}
