/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.entity;

import lombok.Data;

import java.util.Map;

@Data
public class C8StreamDevicePresenceEntity {

    private String _key;
    private String tenant;
    private String fabric;
    private boolean global;
    private String topicName;
    private String fullTopicName;
    private long createdAt;
    private long updatedAt;
    private int producerCount;
    private int subscriptionCount;
    private int consumerCount;
    private Map<String, TopicStats> regionStats;

    @Data
    public static class TopicStats {
        private long timestamp;
        private int producerCount;
        private int subscriptionCount;
        private int consumerCount;
        private String[] producerNames;
        private SubscriptionStats[] subscriptionStats;
    }

    @Data
    public static class SubscriptionStats {
        private String name;
        private int consumerCount;
        private String[] consumerNames;
    }

}
