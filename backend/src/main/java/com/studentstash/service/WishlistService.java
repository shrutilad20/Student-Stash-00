package com.studentstash.service;

import com.studentstash.dto.WishlistResponse;
import com.studentstash.entity.Product;
import com.studentstash.entity.User;
import com.studentstash.entity.Wishlist;
import com.studentstash.exception.BadRequestException;
import com.studentstash.exception.ResourceNotFoundException;
import com.studentstash.repository.ProductRepository;
import com.studentstash.repository.UserRepository;
import com.studentstash.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistResponse addToWishlist(Long productId, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (wishlistRepository.existsByUserIdAndProductId(buyer.getId(), productId)) {
            throw new BadRequestException("Product is already in your wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(buyer);
        wishlist.setProduct(product);

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    @Transactional
    public void removeFromWishlist(Long productId, String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!wishlistRepository.existsByUserIdAndProductId(buyer.getId(), productId)) {
            throw new ResourceNotFoundException("Product not found in your wishlist");
        }

        wishlistRepository.deleteByUserIdAndProductId(buyer.getId(), productId);
    }

    public List<WishlistResponse> getMyWishlist(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return wishlistRepository.findByUserIdOrderByAddedAtDesc(buyer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        String imageUrl = product.getImages().isEmpty()
                ? null
                : product.getImages().get(0).getImageUrl();

        return new WishlistResponse(
                wishlist.getId(),
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                imageUrl,
                product.isActive(),
                wishlist.getAddedAt()
        );
    }

}