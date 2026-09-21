package com.pedidos360.msorders.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super(String.format("Order with id %d not found", id));
    }
}
