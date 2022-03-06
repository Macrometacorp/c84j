/*
 * Copyright (c) 2022 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.c8db.entity.DynamoAttributeDefinition;
import com.c8db.entity.DynamoKeySchemaElement;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DynamoCreateOptions {

    private enum ProjectionType {ALL};

    private List<DynamoAttributeDefinition> attributeDefinitions;
    private String name;
    private List<SecondaryIndex> globalSecondaryIndexes;
    private List<DynamoKeySchemaElement> keySchema;
    private List<SecondaryIndex> localSecondaryIndexes;

    @Data
    public static class SecondaryIndex {
        private String indexName;
        private List<DynamoKeySchemaElement> keySchema;
        private ProjectionType protection = ProjectionType.ALL;
    }
}
