package com.acme.orders.api.services.exceptions;

/**
 * Created by nomar on 8/14/18.
 */
public class GeneralServiceException extends RuntimeException {

    private String resource;

    public GeneralServiceException(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

}