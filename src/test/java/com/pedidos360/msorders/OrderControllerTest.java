package com.pedidos360.msorders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos360.msorders.client.CatalogClient;
import com.pedidos360.msorders.model.Order;
import com.pedidos360.msorders.model.OrderItem;
import com.pedidos360.msorders.model.OrderStatus;
import com.pedidos360.msorders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CatalogClient catalogClient;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        Order order = new Order();
        order.setCustomerId(1L);
        order.setTotal(new BigDecimal("100.00"));

        OrderItem item = new OrderItem();
        item.setProductId(10L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        order.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREADO"));
    }

    @Test
    void shouldAcceptOrderAndDecreaseStock() throws Exception {
        Order order = new Order();
        order.setCustomerId(1L);
        order.setTotal(new BigDecimal("100.00"));
        OrderItem item = new OrderItem();
        item.setProductId(10L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        order.setItems(List.of(item));
        Order savedOrder = orderRepository.save(order);

        // Mock catalog client
        Mockito.doNothing().when(catalogClient).decreaseStock(10L, 2);

        mockMvc.perform(patch("/api/orders/" + savedOrder.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("status", "ACEPTADO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEPTADO"));

        Mockito.verify(catalogClient, Mockito.times(1)).decreaseStock(10L, 2);
    }

    @Test
    void shouldFailToDispatchWhenCreated() throws Exception {
        Order order = new Order();
        order.setCustomerId(1L);
        order.setTotal(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.CREADO);
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(patch("/api/orders/" + savedOrder.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("status", "DESPACHADO"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid transition from CREADO to DESPACHADO"));
    }
}
