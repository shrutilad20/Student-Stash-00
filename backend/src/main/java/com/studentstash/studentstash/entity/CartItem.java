package com.studentstash.studentstash.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")

public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    // CART RELATION
    @ManyToOne
    @JoinColumn(name = "cart_id")

    private Cart cart;

    // PRODUCT RELATION
    @ManyToOne
    @JoinColumn(name = "product_id")

    private Product product;

    private Integer quantity;

    // GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}