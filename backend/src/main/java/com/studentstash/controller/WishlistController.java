package com.studentstash.controller;

import com.studentstash.dto.WishlistResponse;
import com.studentstash.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long productId,
                                                            Authentication authentication) {
        WishlistResponse response = wishlistService.addToWishlist(productId, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId,
                                                     Authentication authentication) {
        wishlistService.removeFromWishlist(productId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getMyWishlist(Authentication authentication) {
        return ResponseEntity.ok(wishlistService.getMyWishlist(authentication.getName()));
    }

}