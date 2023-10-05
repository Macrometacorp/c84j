/*
 * Copyright (c) 2023 Macrometa Corp All rights reserved.
 */

package com.c8db.model;

public interface SortMixin<R> {

    String ORDER_PARAMETER = "order";

    <T> T getProperty(String name);

    <T> void setProperty(String name, T value);

    /**
     * @param order The order of returned KVs
     * @return options
     */
    default R order(final Order order) {
        setProperty(ORDER_PARAMETER, order);
        return (R) this;
    }

    /**
     * @return the order of returned KVs
     */
    default Order getOrder() {
        Order order = getProperty(ORDER_PARAMETER);
        if (order == null) {
            return Order.asc;
        }
        return order;
    }

    enum Order {
        asc, desc
    }

}
