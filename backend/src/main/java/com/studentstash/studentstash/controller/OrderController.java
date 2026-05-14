package com.studentstash.studentstash.controller;

import com.studentstash.studentstash.entity.Orders;

import com.studentstash.studentstash.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")

@RequiredArgsConstructor

public class OrderController {

    private final OrderService orderService;

    // PLACE ORDER
    @PostMapping("/place")

    public Orders placeOrder(
            Authentication authentication) {

        return orderService.placeOrder(
                authentication.getName()
        );
    }

    // GET USER ORDERS
    @GetMapping

    public List<Orders> getUserOrders(
            Authentication authentication) {

        return orderService.getUserOrders(
                authentication.getName()
        );
    }
}