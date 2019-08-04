package com.acme.orders.api.integrations;

import com.acme.orders.api.integrations.lib.catalogue.Product;

/**
 * Created by nomar on 8/20/18.
 */
public interface CatalogueClient {

    Product findProductById(String id);

}
