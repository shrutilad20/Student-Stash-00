package com.studentstash.service;

import com.studentstash.dto.OrderItemResponse;
import com.studentstash.dto.OrderResponse;
import com.studentstash.entity.*;
import com.studentstash.exception.BadRequestException;
import com.studentstash.exception.ResourceNotFoundException;
import com.studentstash.repository.OrderRepository;
import com.studentstash.repository.ProductRepository;
import com.studentstash.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    /**
     * Called after payment succeeds (directly today for testing;
     * will be called from PaymentService in Phase 9).
     */
    @Transactional
    public OrderResponse placeOrder(String buyerEmail, String shippingAddress) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var cart = cartService.getCart(buyerEmail);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setBuyer(buyer);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress(shippingAddress);

        BigDecimal total = BigDecimal.ZERO;

        for (var cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + cartItem.getProductId()));

            if (!product.isActive()) {
                throw new BadRequestException(
                        "Product no longer available: " + product.getTitle());
            }

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for: " + product.getTitle()
                                + " (available: " + product.getQuantity() + ")");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setSeller(product.getSeller());
            item.setProductTitleSnapshot(product.getTitle());
            item.setUnitPriceSnapshot(product.getPrice());
            item.setQuantity(cartItem.getQuantity());

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            item.setSubtotal(subtotal);

            order.getItems().add(item);
            total = total.add(subtotal);

            // Deduct stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        cartService.clearCart(buyerEmail);

        return mapToResponse(saved);
    }

    public List<OrderResponse> getMyOrders(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String requesterEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isBuyer = order.getBuyer().getEmail().equals(requesterEmail);
        boolean isSellerOnOrder = order.getItems().stream()
                .anyMatch(item -> item.getSeller().getEmail().equals(requesterEmail));

        if (!isBuyer && !isSellerOnOrder) {
            throw new BadRequestException("You do not have permission to view this order");
        }

        return mapToResponse(order);
    }

    public List<OrderResponse> getSellerOrders(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return orderRepository.findOrdersContainingSellerItems(seller.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, String sellerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isSellerOnOrder = order.getItems().stream()
                .anyMatch(item -> item.getSeller().getEmail().equals(sellerEmail));

        if (!isSellerOnOrder) {
            throw new BadRequestException("You do not have permission to update this order");
        }

        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        return mapToResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, String buyerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getBuyer().getEmail().equals(buyerEmail)) {
            throw new BadRequestException("You do not have permission to cancel this order");
        }

        validateStatusTransition(order.getStatus(), OrderStatus.CANCELLED);

        // Restock every item
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        boolean valid = switch (current) {
            case CONFIRMED -> target == OrderStatus.SHIPPED || target == OrderStatus.CANCELLED;
            case SHIPPED -> target == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    "Cannot change order status from " + current + " to " + target);
        }
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "SS-" + datePart + "-" + randomPart;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProductTitleSnapshot(),
                        item.getUnitPriceSnapshot(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getSeller().getFullName()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                itemResponses,
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt()
        );
    }

}