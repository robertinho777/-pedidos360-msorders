package com.pedidos360.msorders.service;

import com.pedidos360.msorders.client.CatalogClient;
import com.pedidos360.msorders.exception.InvalidTransitionException;
import com.pedidos360.msorders.exception.OrderNotFoundException;
import com.pedidos360.msorders.model.Order;
import com.pedidos360.msorders.model.OrderStatus;
import com.pedidos360.msorders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            OrderStatus.CREADO,         Set.of(OrderStatus.ACEPTADO, OrderStatus.CANCELADO),
            OrderStatus.ACEPTADO,       Set.of(OrderStatus.EN_PREPARACION),
            OrderStatus.EN_PREPARACION, Set.of(OrderStatus.DESPACHADO),
            OrderStatus.DESPACHADO,     Set.of(OrderStatus.ENTREGADO),
            OrderStatus.ENTREGADO,      Set.of(),
            OrderStatus.CANCELADO,      Set.of()
    );

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREADO);
        return orderRepository.save(order);
    }

    public List<Order> getOrders(Long customerId) {
        return getOrders(customerId, null);
    }

    public List<Order> getOrders(Long customerId, String customerEmail) {
        if (customerEmail != null && !customerEmail.isBlank()) {
            return orderRepository.findByCustomerEmailIgnoreCase(customerEmail);
        }
        if (customerId != null) {
            return orderRepository.findByCustomerId(customerId);
        }
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public Order changeStatus(Long id, OrderStatus nextStatus) {
        Order order = getOrder(id);

        if (!ALLOWED.get(order.getStatus()).contains(nextStatus)) {
            throw new InvalidTransitionException(order.getStatus(), nextStatus);
        }

        if (nextStatus == OrderStatus.ACEPTADO) {
            order.getItems().forEach(i -> catalogClient.decreaseStock(i.getProductId(), i.getQuantity()));
        }

        order.setStatus(nextStatus);
        return orderRepository.save(order);
    }
}
