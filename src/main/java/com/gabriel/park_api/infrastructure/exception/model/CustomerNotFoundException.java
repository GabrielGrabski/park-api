package com.gabriel.park_api.infrastructure.exception.model;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
