package com.studentstash.studentstash.service;

import com.studentstash.studentstash.entity.*;

import com.studentstash.studentstash.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    // ADD TO CART
    public String addToCart(
            Long productId,
            Integer quantity,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));

        // FIND OR CREATE CART
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });

        // CREATE CART ITEM
        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);

        cartItem.setProduct(product);

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return "Product Added To Cart";
    }

    // GET USER CART
    public Cart getUserCart(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart Not Found"));
    }

    // REMOVE CART ITEM
    public String removeCartItem(Long cartItemId) {

        cartItemRepository.deleteById(cartItemId);

        return "Cart Item Removed";
    }
}