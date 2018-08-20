package com.acme.orders.lib.v1;

import com.acme.orders.lib.v1.common.BaseType;

import java.util.Set;

/**
 * Created by nomar on 8/1/18.
 */
public class Order extends BaseType {

    private OrderStatus status;
    private Set<OrderItem> cart;
    private String customerId;
    private String transactionId;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Set<OrderItem> getCart() {
        return cart;
    }

    public void setCart(Set<OrderItem> cart) {
        this.cart = cart;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
