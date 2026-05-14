package com.studentstash.studentstash.controller;

import com.studentstash.studentstash.dto.ProductRequest;
import com.studentstash.studentstash.entity.Product;
import com.studentstash.studentstash.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")

@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ADD PRODUCT
    @PostMapping
    public Product addProduct(
            @RequestBody ProductRequest request,
            Authentication authentication) {

        String sellerEmail = authentication.getName();

        return productService.addProduct(
                request,
                sellerEmail
        );
    }

    // GET ALL PRODUCTS
    @GetMapping
    public List<Product> getAllProducts() {

        return productService.getAllProducts();
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public Product getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id);
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        return productService.deleteProduct(id);
    }

    // SEARCH PRODUCT
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String keyword) {

        return productService.searchProducts(keyword);
    }
}