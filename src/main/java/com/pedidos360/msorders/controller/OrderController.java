package com.pedidos360.msorders.controller;

import com.pedidos360.msorders.model.Order;
import com.pedidos360.msorders.model.OrderStatus;
import com.pedidos360.msorders.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(
            @RequestBody Order order,
            @RequestHeader(value = "X-Customer-Email", required = false) String customerEmailHeader,
            @RequestHeader(value = "X-Customer-Name", required = false) String customerNameHeader) {
        if ((order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) && customerEmailHeader != null) {
            order.setCustomerEmail(customerEmailHeader);
        }
        if ((order.getCustomerName() == null || order.getCustomerName().isBlank()) && customerNameHeader != null) {
            order.setCustomerName(customerNameHeader);
        }
        return orderService.createOrder(order);
    }

    @GetMapping
    public List<Order> getOrders(
            @RequestHeader(value = "X-Customer-Id", required = false) Long customerId,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestHeader(value = "X-Customer-Email", required = false) String customerEmailHeader) {
        String email = customerEmail != null && !customerEmail.isBlank() ? customerEmail : customerEmailHeader;
        return orderService.getOrders(customerId, email);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    public Order changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        OrderStatus status = OrderStatus.valueOf(body.get("status").toUpperCase());
        return orderService.changeStatus(id, status);
    }
}
