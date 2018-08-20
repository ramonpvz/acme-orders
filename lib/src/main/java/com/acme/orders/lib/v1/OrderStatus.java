package com.acme.orders.lib.v1;

/**
 * Created by nomar on 8/1/18.
 */
public enum OrderStatus {

    NEW(1), COMPLETED(2), CANCELED(3);

    private int value;

    private OrderStatus(int value) {
        this.value = value;
    }

}
