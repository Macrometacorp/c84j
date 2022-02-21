/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */

package com.c8db.internal;

import com.c8db.C8Alerts;
import com.c8db.C8DBException;
import com.c8db.entity.AlertEntity;

import java.util.Collection;
import java.util.Map;

public class C8AlertsImpl extends InternalC8Alerts<C8DBImpl, C8DatabaseImpl, C8ExecutorSync> implements C8Alerts {

    protected C8AlertsImpl(final C8DatabaseImpl db) {
        super(db);
    }

    @Override
    protected C8ExecutorSync executor() {
        return executor;
    }

    @Override
    public Collection<AlertEntity> getAlerts(Map<String, String> queryParamMap) throws C8DBException {
        return executor.execute(getAlertRequest(queryParamMap), alertsListResponseDeserializer());
    }

    @Override
    public AlertEntity updateAlerts(String updateParam, Map<String, String> queryParamMap) throws C8DBException {
        return executor.execute(updateAlertRequest(updateParam, queryParamMap), alertsResponseDeserializer());
    }

    @Override
    public AlertEntity createAlerts(AlertEntity entity) throws C8DBException {
        return executor.execute(createAlertRequest(entity), alertsResponseDeserializer());
    }
}
