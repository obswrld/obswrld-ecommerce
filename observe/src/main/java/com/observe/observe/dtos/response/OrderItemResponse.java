package com.observe.observe.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private UUID id;

    private String productName;

    private UUID productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}