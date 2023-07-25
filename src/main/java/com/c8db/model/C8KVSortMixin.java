/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

import com.c8db.C8KeyValue;

public interface C8KVSortMixin<R> {

    String ORDER_PARAMETER = "order";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param order The order of returned KVs
     * @return options
     */
    default R order(final C8KeyValue.Order order) {
        setProperty(ORDER_PARAMETER, order);
        return (R) this;
    }

    /**
     * @return the order of returned KVs
     */
    default C8KeyValue.Order getOrder() {
        C8KeyValue.Order order = getProperty(ORDER_PARAMETER);
        if (order == null) {
            return C8KeyValue.Order.asc;
        }
        return order;
    }

}
