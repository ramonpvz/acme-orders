package com.acme.orders.api.services;

import com.acme.orders.lib.v1.Order;

import java.util.List;

/**
 * Created by nomar on 8/14/18.
 */
public interface OrderService {

    Order findOrderById(String id);

    List<Order> findOrders(Integer limit, Integer offset);

    Long findOrdersCount();

    Order createOrder(Order order);

    Order completeOrder(String id);

    Order cancelOrder(String id);

}
