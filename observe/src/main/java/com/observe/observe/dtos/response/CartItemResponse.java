package com.observe.observe.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private UUID id;

    private UUID productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;
}