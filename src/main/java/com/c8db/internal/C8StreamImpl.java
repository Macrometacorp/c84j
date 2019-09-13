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

package com.c8db.internal;

import java.util.Collection;

import com.c8db.C8Stream;
import com.c8db.entity.C8StreamBacklogEntity;
import com.c8db.entity.C8StreamStatisticsEntity;

/**
 *
 */
public class C8StreamImpl extends InternalC8Stream<C8DBImpl, C8DatabaseImpl, C8ExecutorSync>
        implements C8Stream {

    protected C8StreamImpl(final C8DatabaseImpl db, final String name) {
        super(db, name);
    }

    @Override
    public C8StreamBacklogEntity getBacklog(final boolean isLocal) {
        return executor.execute(getC8StreamBacklogRequest(isLocal), getC8StreamBacklogResponseDeserializer());
    }

    @Override
    public C8StreamStatisticsEntity getStatistics(final boolean isLocal) {
        return executor.execute(getC8StreamStatisticsRequest(isLocal), getC8StreamStatisticsResponseDeserializer());
    }

    @Override
    public void terminate(final boolean isLocal) {
        executor.execute(terminateC8StreamRequest(isLocal), Void.class);
    }

    @Override
    public Collection<String> getSubscriptions(final boolean isLocal) {
        return executor.execute(getC8StreamSubscriptionsRequest(isLocal),
                getC8StreamSubscriptionsResponseDeserializer());
    }

    @Override
    public void skipMessages(final String subscriptionName, final int numberOfMessages, final boolean isLocal) {
        executor.execute(skipMessagesRequest(subscriptionName, numberOfMessages, isLocal), Void.class);
    }

    @Override
    public void skipAllMessages(final String subscriptionName, final boolean isLocal) {
        executor.execute(skipAllMessagesRequest(subscriptionName, isLocal), Void.class);
    }

    @Override
    public void resetCursorToTimestamp(final String subscriptionName, final int timestamp, final boolean isLocal) {
        executor.execute(resetCursorRequest(subscriptionName, timestamp, isLocal), Void.class);
    }

    @Override
    public void resetCursor(final String subscriptionName, final boolean isLocal) {
        executor.execute(resetCursorRequest(subscriptionName, isLocal), Void.class);
    }

    @Override
    public void expireMessagesInSeconds(final String subscriptionName, final int expireTimeInSeconds,
            final boolean isLocal) {
        executor.execute(expireMessagesRequest(subscriptionName, expireTimeInSeconds, isLocal), Void.class);
    }
    
    @Override
    public void deleteSubscription(final String subscriptionName, final boolean isLocal) {
        executor.execute(deleteSubscriptionRequest(subscriptionName, isLocal), Void.class);
    }
}
