package com.studentstash.studentstash.repository;

import com.studentstash.studentstash.entity.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}