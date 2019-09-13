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
    C8StreamBacklogEntity getBacklog(final boolean isLocal);
    
    /**
     * Get the statistics for the given stream.
     * @param isLocal Operate on a local stream instead of a global one. Default value: false
     * @return
     */
    C8StreamStatisticsEntity getStatistics(final boolean isLocal);
    
    /**
     * Terminate a stream. A stream that is terminated will not accept any more messages to be published and will let consumer to drain existing messages in backlog.
     * @param isLocal Operate on a local stream instead of a global one. Default value: false
     * @return
     */
    void terminate(final boolean isLocal);
    
    /**
     * Get the list of persistent subscriptions for a given stream.
     * @param isLocal Operate on a local stream instead of a global one. Default value: false
     * @return
     */
    Collection<String> getSubscriptions(final boolean isLocal);

    /**
     * Skip num messages on a topic subscription.
     * @param subscriptionName Identification name of the subscription.
     * @param numberOfMessages Number of messages to skip.
     * @param isLocal Operate on a local stream instead of a global one. 
     */
    void skipMessages(final String subscriptionName, int numberOfMessages, boolean isLocal);

    /**
     * Skip all messages on a stream subscription.
     * @param subscriptionName Identification name of the subscription.
     * @param isLocal Operate on a local stream instead of a global one.
     */
    void skipAllMessages(final String subscriptionName, boolean isLocal);

    /**
     * Reset subscription to message position closest to absolute timestamp (in miliseconds).
     * @param subscriptionName Identification name of the subscription.
     * @param timestamp Timestamp in miliseconds.
     * @param isLocal Operate on a local stream instead of a global one.
     */
    void resetCursorToTimestamp(final String subscriptionName, int timestamp, boolean isLocal);

    /**
     * Disconnect all active consumers for a cursor and reset the cursor.
     * @param subscriptionName Identification name of the subscription.
     * @param isLocal Operate on a local stream instead of a global one.
     */
    void resetCursor(String subscriptionName, boolean isLocal);

    /**
     * Expire messages on a stream subscription.
     * @param subscriptionName Identification name of the subscription.
     * @param expireTimeInSeconds Expiration time in seconds.
     * @param isLocal Operate on a local stream instead of a global one.
     */
    void expireMessagesInSeconds(String subscriptionName, int expireTimeInSeconds, boolean isLocal);

    /**
     * Delete a subscription.
     * @param subscriptionName Identification name of the subscription.
     * @param isLocal Operate on a local stream instead of a global one.
     */
    void deleteSubscription(String subscriptionName, boolean isLocal);
}
