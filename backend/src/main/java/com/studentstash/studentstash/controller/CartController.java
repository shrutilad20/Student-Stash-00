package com.studentstash.studentstash.controller;

import com.studentstash.studentstash.entity.Cart;

import com.studentstash.studentstash.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")

@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // ADD TO CART
    @PostMapping("/add")

    public String addToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {

        return cartService.addToCart(
                productId,
                quantity,
                authentication.getName()
        );
    }

    // GET USER CART
    @GetMapping

    public Cart getUserCart(
            Authentication authentication) {

        return cartService.getUserCart(
                authentication.getName()
        );
    }

    // REMOVE ITEM
    @DeleteMapping("/{cartItemId}")

    public String removeCartItem(
            @PathVariable Long cartItemId) {

        return cartService.removeCartItem(cartItemId);
    }
}