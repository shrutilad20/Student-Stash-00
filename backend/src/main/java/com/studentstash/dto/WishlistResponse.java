package com.studentstash.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class WishlistResponse {

    private Long wishlistId;
    private Long productId;
    private String productTitle;
    private BigDecimal productPrice;
    private String productImageUrl; // first image, or null
    private boolean productActive;  // lets frontend show "no longer available"
    private LocalDateTime addedAt;

}