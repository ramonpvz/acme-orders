package com.acme.orders.api.rest.v1;

import com.acme.orders.api.integrations.impl.CatalogueClientImpl;
import com.acme.orders.api.integrations.impl.CustomersClientImpl;
import com.acme.orders.api.models.OrderDAO;
import com.acme.orders.api.models.db.OrderEntity;
import com.acme.orders.api.models.db.OrderItemEntity;
import com.acme.orders.api.rest.v1.mappers.*;
import com.acme.orders.api.rest.v1.resources.OrderResource;
import com.acme.orders.api.services.OrderService;
import com.acme.orders.api.services.impl.OrderServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by nomar on 8/14/18.
 */
public class RestApplication extends Application<RestConfiguration> {

    public static void main(String args[]) throws Exception {
        new RestApplication().run(args);
    }

    private final HibernateBundle<RestConfiguration> hibernate = new HibernateBundle<RestConfiguration>(OrderEntity.class, OrderItemEntity.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(RestConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public void initialize(Bootstrap<RestConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(RestConfiguration configuration, Environment environment) throws Exception {

        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        environment.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //JAX-RX Client
        Client client = ClientBuilder.newClient();

        OrderService orderService = new OrderServiceImpl(
                new OrderDAO(hibernate.getSessionFactory(), environment.metrics()),
                environment.metrics(),
                new CustomersClientImpl(client, configuration.getCustomersUrl()),
                new CatalogueClientImpl(configuration.getCatalogueUrl())
        );

        environment.jersey().register(new OrderResource(orderService));

        environment.jersey().register(EmptyPayloadMapper.class);
        environment.jersey().register(GeneralMapper.class);
        environment.jersey().register(IdMismatchMapper.class);
        environment.jersey().register(ResourceNotFoundMapper.class);
        environment.jersey().register(OrderServiceMapper.class);

    }
}
