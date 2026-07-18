package com.studentstash.repository;

import com.studentstash.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    Page<Product> findByTitleContainingIgnoreCaseAndActiveTrue(String keyword, Pageable pageable);

}