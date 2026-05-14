package com.studentstash.studentstash.repository;

import com.studentstash.studentstash.entity.Product;
import com.studentstash.studentstash.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findBySeller(User seller);

    List<Product> findByCategory(String category);

    List<Product> findByTitleContainingIgnoreCase(String keyword);
}