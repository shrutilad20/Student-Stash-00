package com.studentstash.studentstash.repository;

import com.studentstash.studentstash.entity.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {
}