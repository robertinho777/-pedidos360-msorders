package com.pedidos360.msorders;

import com.pedidos360.msorders.model.Order;
import com.pedidos360.msorders.model.OrderItem;
import com.pedidos360.msorders.model.OrderStatus;
import com.pedidos360.msorders.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrdersDataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public OrdersDataInitializer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (orderRepository.count() == 0) {
            // Pedido 1: CREADO
            Order o1 = new Order();
            o1.setCustomerId(1L);
            o1.setStatus(OrderStatus.CREADO);
            o1.setCreatedAt(LocalDateTime.now().minusHours(2));
            o1.setTotal(new BigDecimal("129.98"));

            OrderItem i1 = new OrderItem();
            i1.setProductId(1L); // Teclado
            i1.setQuantity(1);
            i1.setUnitPrice(new BigDecimal("79.99"));

            OrderItem i2 = new OrderItem();
            i2.setProductId(2L); // Mouse
            i2.setQuantity(1);
            i2.setUnitPrice(new BigDecimal("49.99"));

            o1.setItems(List.of(i1, i2));

            // Pedido 2: ACEPTADO
            Order o2 = new Order();
            o2.setCustomerId(1L);
            o2.setStatus(OrderStatus.ACEPTADO);
            o2.setCreatedAt(LocalDateTime.now().minusDays(1));
            o2.setTotal(new BigDecimal("299.99"));

            OrderItem i3 = new OrderItem();
            i3.setProductId(3L); // Monitor
            i3.setQuantity(1);
            i3.setUnitPrice(new BigDecimal("299.99"));

            o2.setItems(List.of(i3));

            orderRepository.saveAll(List.of(o1, o2));
        }
    }
}
