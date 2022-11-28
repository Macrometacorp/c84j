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

import com.c8db.entity.C8EventIDEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.c8db.C8DBException;
import com.c8db.C8Event;
import com.c8db.entity.C8EventEntity;
import com.c8db.model.DocumentDeleteOptions;
import com.c8db.model.DocumentReadOptions;
import com.c8db.model.C8EventCreate;

/**
 */
public class C8EventImpl extends InternalC8Event<C8DBImpl, C8DatabaseImpl, C8ExecutorSync> implements C8Event {

    private static final Logger LOGGER = LoggerFactory.getLogger(C8Event.class);

    protected C8EventImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    public C8EventIDEntity insertEvent(C8EventCreate value) throws C8DBException {
        return executor.execute(insertEventRequest(value), insertEventResponseDeserializer());
    }

    @Override
    public C8EventEntity getEvent(String key) throws C8DBException {
        return getEvent(key, new DocumentReadOptions());
    }

    @Override
    public C8EventEntity getEvent(String key, DocumentReadOptions options) throws C8DBException {
        try {
            return executor.execute(getEventRequest(key, options), C8EventEntity.class);
        } catch (final C8DBException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            }

            // handle Response: 404, Error: 1655 - transaction not found
            if (e.getErrorNum() != null && e.getErrorNum() == 1655) {
                throw e;
            }

            if ((e.getResponseCode() != null
                    && (e.getResponseCode() == 404 || e.getResponseCode() == 304 || e.getResponseCode() == 412))
                    && (options == null || options.isCatchException())) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public Collection<C8EventEntity> getEvents() throws C8DBException {
        return executor.execute(getEventsRequest(), getEventsResponseDeserializer());
    }

    @Override
    public C8EventIDEntity deleteEvent(String key) throws C8DBException {
        return executor.execute(deleteEventRequest(key, new DocumentDeleteOptions()), deleteEventResponseDeserializer());
    }

    @Override
    public Collection<C8EventIDEntity> deleteEvents(Collection<?> keys) throws C8DBException {
        return executor.execute(deleteEventsRequest(keys, new DocumentDeleteOptions()), deleteEventsResponseDeserializer());
    }

}
