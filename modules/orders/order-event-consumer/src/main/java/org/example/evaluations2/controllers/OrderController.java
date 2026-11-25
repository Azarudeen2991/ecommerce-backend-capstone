package org.example.evaluations2.controllers;

import org.example.evaluations2.dtos.OrderEvent;
import org.example.evaluations2.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderEvent orderEvent) {


        if (orderEvent.getPrice() <= 0) {
            return handleIllegalArgumentException(new IllegalArgumentException("Price must be greater than zero"));
        }
        if (orderEvent.getQuantity() <= 0) {
            return handleIllegalArgumentException(new IllegalArgumentException("Quantity must be greater than zero"));
        }

        orderService.createOrder(orderEvent);
        return ResponseEntity.ok("Order placed successfully");


    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


}

