package com.interstore.interstore_backend.controller;

import com.interstore.interstore_backend.dto.CheckoutRequest;
import com.interstore.interstore_backend.entity.Ordine;
import com.interstore.interstore_backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Ordine> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ordine> getOrderById(@PathVariable Long id) {
        Optional<Ordine> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ordine createOrder(@RequestBody Ordine order) {
        return orderService.saveOrder(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        try {
            Ordine order = orderService.checkout(checkoutRequest);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<?> checkoutConUserId(
            @PathVariable Long userId,
            @RequestBody CheckoutRequest checkoutRequest) {
        try {
            checkoutRequest.setUserId(userId);
            Ordine order = orderService.checkout(checkoutRequest);
            return ResponseEntity.ok(order);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }



}
