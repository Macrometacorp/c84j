package com.c8db.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class C8DynamoItemEntity {
    Map<String,Object> ConsumedCapacity;
    Map<String,Object> Item;

    List<Map<String,Object>> Items;
    Map<String,Map<String,Object>> LastEvaluatedKey;
    Long ScannedCount;
    Long Count;


}
