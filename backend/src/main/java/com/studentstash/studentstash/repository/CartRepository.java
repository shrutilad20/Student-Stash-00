package com.studentstash.studentstash.repository;

import com.studentstash.studentstash.entity.Cart;
import com.studentstash.studentstash.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository
        extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}