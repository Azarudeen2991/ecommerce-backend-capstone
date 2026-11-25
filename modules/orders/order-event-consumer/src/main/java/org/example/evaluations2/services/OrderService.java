package org.example.evaluations2.services;

import org.example.evaluations2.dtos.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderService(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void createOrder(OrderEvent order) {
        order.setStatus("CREATED");
        order.setOrderId(UUID.randomUUID().toString());

        // Add message at topic "order-events" in kafka
        kafkaTemplate.send("order-events", order.getOrderId(), order);

    }
}