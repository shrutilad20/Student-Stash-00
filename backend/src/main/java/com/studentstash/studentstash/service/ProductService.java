package com.studentstash.studentstash.service;

import com.studentstash.studentstash.dto.ProductRequest;
import com.studentstash.studentstash.entity.Product;
import com.studentstash.studentstash.entity.User;
import com.studentstash.studentstash.repository.ProductRepository;
import com.studentstash.studentstash.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    // ADD PRODUCT
    public Product addProduct(
            ProductRequest request,
            String sellerEmail) {

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() ->
                        new RuntimeException("Seller Not Found"));

        Product product = new Product();

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setSubject(request.getSubject());
        product.setUniversity(request.getUniversity());
        product.setSemester(request.getSemester());
        product.setConditionType(request.getConditionType());
        product.setImageUrl(request.getImageUrl());

        product.setSeller(seller);

        return productRepository.save(product);
    }

    // GET ALL PRODUCTS
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // GET PRODUCT BY ID
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Not Found"));
    }

    // DELETE PRODUCT
    public String deleteProduct(Long id) {

        productRepository.deleteById(id);

        return "Product Deleted Successfully";
    }

    // SEARCH PRODUCT
    public List<Product> searchProducts(String keyword) {

        return productRepository
                .findByTitleContainingIgnoreCase(keyword);
    }
}