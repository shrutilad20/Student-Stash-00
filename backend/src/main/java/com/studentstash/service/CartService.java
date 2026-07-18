package com.studentstash.service;

import com.studentstash.dto.CartItemResponse;
import com.studentstash.dto.CartResponse;
import com.studentstash.entity.Cart;
import com.studentstash.entity.CartItem;
import com.studentstash.entity.Product;
import com.studentstash.entity.User;
import com.studentstash.exception.BadRequestException;
import com.studentstash.exception.ResourceNotFoundException;
import com.studentstash.repository.CartItemRepository;
import com.studentstash.repository.CartRepository;
import com.studentstash.repository.ProductRepository;
import com.studentstash.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse addItemToCart(Long productId, Integer quantity, String buyerEmail) {
        Cart cart = getOrCreateCart(buyerEmail);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new BadRequestException("This product is no longer available");
        }

        if (product.getSeller().getId().equals(cart.getUser().getId())) {
            throw new BadRequestException("You cannot add your own product to cart");
        }

        var existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            validateStock(product, newQuantity);
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            validateStock(product, quantity);
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return getCart(buyerEmail);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long productId, Integer quantity, String buyerEmail) {
        Cart cart = getOrCreateCart(buyerEmail);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in your cart"));

        validateStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return getCart(buyerEmail);
    }

    @Transactional
    public CartResponse removeItemFromCart(Long productId, String buyerEmail) {
        Cart cart = getOrCreateCart(buyerEmail);

        if (cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).isEmpty()) {
            throw new ResourceNotFoundException("Product not found in your cart");
        }

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        return getCart(buyerEmail);
    }

    @Transactional
    public void clearCart(String buyerEmail) {
        Cart cart = getOrCreateCart(buyerEmail);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    public CartResponse getCart(String buyerEmail) {
        Cart cart = getOrCreateCart(buyerEmail);

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        BigDecimal grandTotal = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return new CartResponse(cart.getId(), itemResponses, grandTotal, totalItems);
    }

    private Cart getOrCreateCart(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUserId(buyer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(buyer);
                    return cartRepository.save(newCart);
                });
    }

    private void validateStock(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getQuantity()) {
            throw new BadRequestException(
                    "Only " + product.getQuantity() + " unit(s) available for: " + product.getTitle());
        }
    }

    private CartItemResponse mapToItemResponse(CartItem item) {
        Product product = item.getProduct();
        String imageUrl = product.getImages().isEmpty()
                ? null
                : product.getImages().get(0).getImageUrl();

        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getTitle(),
                imageUrl,
                product.getPrice(),
                item.getQuantity(),
                subtotal,
                product.isActive(),
                product.getQuantity()
        );
    }

}