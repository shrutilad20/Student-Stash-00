package com.studentstash.studentstash.service;

import com.studentstash.studentstash.entity.*;

import com.studentstash.studentstash.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrdersRepository ordersRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    // PLACE ORDER
    public Orders placeOrder(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart Not Found"));

        Orders order = new Orders();

        order.setUser(user);

        double total = 0;

        // LOOP CART ITEMS
        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(cartItem.getProduct());

            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            total += cartItem.getProduct().getPrice()
                    * cartItem.getQuantity();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);

        Orders savedOrder =
                ordersRepository.save(order);

        // CLEAR CART
        cart.getItems().clear();

        cartRepository.save(cart);

        return savedOrder;
    }

    // GET USER ORDERS
    public List<Orders> getUserOrders(
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        return ordersRepository.findByUser(user);
    }
}