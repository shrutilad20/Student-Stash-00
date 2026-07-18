package com.studentstash.dto;

import com.studentstash.entity.ProductCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private ProductCondition condition;
    private String categoryName;
    private String sellerName;
    private Long sellerId;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

}