package com.observe.observe.dtos.response;

import com.observe.observe.models.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private ProductStatus status;

    private String slug;

    private UUID sellerId;

    private UUID categoryId;

    private LocalDateTime createdAt;
    
}