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
import com.c8db.Service;
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
    public C8StreamBacklogEntity getBacklog() {
        return executor.execute(getC8StreamBacklogRequest(), getC8StreamBacklogResponseDeserializer(), null, Service.C8STREAMS);
    }

    @Override
    public C8StreamStatisticsEntity getStatistics() {
        return executor.execute(getC8StreamStatisticsRequest(), getC8StreamStatisticsResponseDeserializer(), null, Service.C8STREAMS);
    }

    @Override
    public void delete() {
        executor.execute(deleteC8StreamRequest(), Void.class, null, Service.C8STREAMS);
    }

    @Override
    public Collection<String> getSubscriptions() {
        return executor.execute(getC8StreamSubscriptionsRequest(),
                getC8StreamSubscriptionsResponseDeserializer(), null, Service.C8STREAMS);
    }

    @Override
    public void expireMessagesInSeconds(final int expireTimeInSeconds) {
        executor.execute(expireMessagesRequest(expireTimeInSeconds), Void.class, null, Service.C8STREAMS);
    }
    
    @Override
    public void deleteSubscription(final String subscriptionName) {
        executor.execute(deleteSubscriptionRequest(subscriptionName), Void.class, null, Service.C8STREAMS);
    }
}
