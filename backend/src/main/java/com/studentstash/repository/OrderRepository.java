package com.studentstash.repository;

import com.studentstash.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.seller.id = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersContainingSellerItems(@Param("sellerId") Long sellerId);

}