package com.acme.orders.api.services.impl;

import com.acme.orders.api.mapper.OrderMapper;
import com.acme.orders.api.models.OrderDAO;
import com.acme.orders.api.models.db.OrderEntity;
import com.acme.orders.api.services.OrderService;
import com.acme.orders.api.services.exceptions.EmptyPayloadException;
import com.acme.orders.api.services.exceptions.OrderServiceException;
import com.acme.orders.api.services.exceptions.ResourceNotFoundException;
import com.acme.orders.lib.v1.Order;
import com.acme.orders.lib.v1.OrderStatus;
import com.acme.orders.lib.v1.common.OrderServiceErrorCode;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nomar on 8/14/18.
 */
public class OrderServiceImpl implements OrderService {

    private OrderDAO orderDAO;

    private Meter createMeter;
    private Meter completeMeter;
    private Meter canceledMeter;

    private Counter processingCounter;
    private Histogram cartHistogram;

    public OrderServiceImpl(OrderDAO orderDAO, MetricRegistry metricRegistry) {
        this.orderDAO = orderDAO;

        this.createMeter = metricRegistry.meter(OrderServiceImpl.class.getName() + ".create-order");
        this.completeMeter = metricRegistry.meter(OrderServiceImpl.class.getName() + ".complete-order");
        this.canceledMeter = metricRegistry.meter(OrderServiceImpl.class.getName() + ".cancel-order");

        this.processingCounter = metricRegistry.counter(OrderServiceImpl.class.getName() + ".orders-processing");
        this.cartHistogram = metricRegistry.histogram(OrderServiceImpl.class.getName() + ".orders-cart-histogram");

    }
    
    @Override
    public Order findOrderById(String id) {

        OrderEntity orderEntity = orderDAO.findById(id);

        if (orderEntity == null)
            throw new ResourceNotFoundException(Order.class.getSimpleName(), id);

        return OrderMapper.toOrder(orderEntity);
    }

    @Override
    public List<Order> findOrders(Integer limit, Integer offset) {

        List<OrderEntity> orderEntities = orderDAO.findAll(limit, offset);
        
        return orderEntities.stream().map(OrderMapper::toOrder).collect(Collectors.toList());
        
    }

    @Override
    public Long findOrdersCount() {

        return orderDAO.findAllCount();

    }

    @Override
    public Order createOrder(Order order) {
        
        if (order == null) {
            throw new EmptyPayloadException(Order.class.getSimpleName());
        }

        if (order.getCart() == null || order.getCart().isEmpty()) {
            throw new OrderServiceException(OrderServiceErrorCode.ORDER_CART_EMPTY);
        }

        Date date = Date.from(Instant.now());
        
        OrderEntity orderEntity = OrderMapper.toOrderEntity(order);
        orderEntity.setId(null);
        orderEntity.setUpdatedAt(date);
        orderEntity.setCreatedAt(date);
        orderEntity.setStatus(OrderStatus.NEW);

        orderDAO.create(orderEntity);

        createMeter.mark();
        cartHistogram.update(orderEntity.getCart().size());

        return OrderMapper.toOrder(orderEntity);

    }

    @Override
    public Order completeOrder(String id) {

        OrderEntity orderEntity = orderDAO.findById(id);

        if (orderEntity == null) {
            throw new ResourceNotFoundException(Order.class.getSimpleName(), id);
        }

        if (!orderEntity.getStatus().equals(OrderStatus.NEW)) {
            throw new OrderServiceException(OrderServiceErrorCode.ORDER_STATE_INCORRECT);
        }

        processingCounter.inc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        processingCounter.dec();

        orderEntity.setStatus(OrderStatus.COMPLETED);

        completeMeter.mark();

        return OrderMapper.toOrder(orderEntity);
    }

    @Override
    public Order cancelOrder(String id) {

        OrderEntity orderEntity = orderDAO.findById(id);

        if (orderEntity == null)  {
            throw new ResourceNotFoundException(Order.class.getSimpleName(), id);
        }

        if (!orderEntity.getStatus().equals(OrderStatus.NEW)) {
            throw new OrderServiceException(OrderServiceErrorCode.ORDER_STATE_INCORRECT);
        }

        orderEntity.setStatus(OrderStatus.CANCELED);

        canceledMeter.mark();

        return OrderMapper.toOrder(orderEntity);

    }

}
