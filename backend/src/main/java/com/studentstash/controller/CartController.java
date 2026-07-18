package com.studentstash.controller;

import com.studentstash.dto.CartItemRequest;
import com.studentstash.dto.CartResponse;
import com.studentstash.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemRequest request,
                                                  Authentication authentication) {
        CartResponse response = cartService.addItemToCart(
                request.getProductId(), request.getQuantity(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long productId,
                                                     @RequestParam Integer quantity,
                                                     Authentication authentication) {
        CartResponse response = cartService.updateItemQuantity(
                productId, quantity, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId,
                                                     Authentication authentication) {
        return ResponseEntity.ok(cartService.removeItemFromCart(productId, authentication.getName()));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }

}