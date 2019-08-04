package com.acme.orders.api.integrations;

import com.acme.orders.api.integrations.lib.catalogue.Customer;

/**
 * Created by nomar on 8/20/18.
 */
public interface CustomersClient {

    Customer findCustomerById(String id);

}
