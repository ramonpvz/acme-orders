package com.acme.orders.api.integrations.impl;

import com.acme.orders.api.integrations.CustomersClient;
import com.acme.orders.api.integrations.lib.catalogue.Customer;
import com.acme.orders.api.services.exceptions.ResourceNotFoundException;
import com.acme.orders.lib.v1.common.ApiError;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by nomar on 8/20/18.
 */
public class CustomersClientImpl implements CustomersClient {

    private WebTarget webTarget;

    public CustomersClientImpl(Client client, String customersUrl) {
        this.webTarget = client.target(customersUrl).register(JacksonJsonProvider.class);
    }

    @Override
    public Customer findCustomerById(String id) {
        try {
            return webTarget.path("customers").path(id).request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "1234")
                    .get(Customer.class);
        } catch (WebApplicationException e) {
            ApiError error = e.getResponse().readEntity(ApiError.class);
            switch (error.getStatus()){
                case 404:
                    throw new ResourceNotFoundException(Customer.class.getSimpleName(), id);
            }
            throw e;
        }
    }

}
