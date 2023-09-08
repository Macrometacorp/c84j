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
 *
 */

package com.c8db;

import java.util.Collection;

import com.c8db.entity.C8StreamBacklogEntity;
import com.c8db.entity.C8StreamDevicePresenceEntity;
import com.c8db.entity.C8StreamStatisticsEntity;

/**
 * Interface for operations on ArangoDB graph level.
 *
 * @see <a href="https://docs.arangodb.com/current/HTTP/Gharial/">API
 *      Documentation</a>
 */
public interface C8Stream extends C8SerializationAccessor {

    /**
     * The the handler of the database the named graph is within
     *
     * @return database handler
     */
    C8Database db();

    /**
     * The name of the collection
     *
     * @return collection name
     */
    String name();

    /**
     * Get estimated backlog for offline stream.
     * 
     * @return
     */
    C8StreamBacklogEntity getBacklog();
    
    /**
     * Get the statistics for the given stream.
     * @return
     */
    C8StreamStatisticsEntity getStatistics();
    
    /**
     * Delete a stream. A stream that is deleted will not accept any more messages to be published and will let consumers drain existing messages in a backlog.
     */
    void delete();
    
    /**
     * Get the list of persistent subscriptions for a given stream.
     * @return
     */
    Collection<String> getSubscriptions();

    /**
     * Expire messages on a stream subscription.
     * @param expireTimeInSeconds Expiration time in seconds.
     */
    void expireMessagesInSeconds(int expireTimeInSeconds);

    /**
     * Delete a subscription.
     * @param subscriptionName Identification name of the subscription.
     */
    void deleteSubscription(String subscriptionName);

    /**
     * Retrieves device presence based on specified filters.
     *
     * @param regionFilter       The region filter to narrow down the filtering.
     * @param producerFilter     The producer filter to narrow down the filtering.
     * @param subscriptionFilter The subscription filter to narrow down the filtering.
     * @param consumerFilter     The consumer filter to narrow down the filtering.
     * @return The C8StreamDevicePresenceEntity representing the device presence.
     */
    C8StreamDevicePresenceEntity getDevicePresence(String regionFilter, String producerFilter,
                                                   String subscriptionFilter, String consumerFilter);
}
