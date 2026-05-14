package com.studentstash.studentstash.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")

public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    // USER
    @ManyToOne
    @JoinColumn(name = "user_id")

    private User user;

    // ORDER ITEMS
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<OrderItem> items =
            new ArrayList<>();

    private Double totalAmount;

    @Enumerated(EnumType.STRING)

    private OrderStatus status =
            OrderStatus.PENDING;

    private LocalDateTime createdAt =
            LocalDateTime.now();

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}