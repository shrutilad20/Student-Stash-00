package com.studentstash.studentstash.repository;

import com.studentstash.studentstash.entity.Orders;
import com.studentstash.studentstash.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository
        extends JpaRepository<Orders, Long> {

    List<Orders> findByUser(User user);
}