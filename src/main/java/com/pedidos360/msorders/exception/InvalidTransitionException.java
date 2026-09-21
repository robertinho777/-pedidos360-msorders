package com.pedidos360.msorders.exception;

import com.pedidos360.msorders.model.OrderStatus;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(OrderStatus currentStatus, OrderStatus nextStatus) {
        super(String.format("Invalid transition from %s to %s", currentStatus, nextStatus));
    }
}
