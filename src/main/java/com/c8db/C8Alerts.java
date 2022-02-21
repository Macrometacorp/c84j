/*
 *
 *  * Copyright (c) 2022 Macrometa Corp All rights reserved
 *
 */

package com.c8db;

import com.c8db.entity.AlertEntity;

import java.util.Collection;
import java.util.Map;

public interface C8Alerts {

    /**
     * Gets Alerts
     * @param queryParamMap
     * @return
     * @throws C8DBException
     */
    Collection<AlertEntity> getAlerts(Map<String, String> queryParamMap) throws C8DBException;

    /**
     * Updates an alert
     * @param updateParam
     * @param queryParamMap
     * @return
     * @throws C8DBException
     */
    public AlertEntity updateAlerts(String updateParam, Map<String, String> queryParamMap) throws C8DBException;

    /**
     * Creates an Alert
     * @param entity
     * @return
     * @throws C8DBException
     */
    public AlertEntity createAlerts(AlertEntity entity) throws C8DBException;
}
