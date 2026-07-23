package com.observe.observe.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.observe.observe.models.OrderStatus;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private UUID id;

    private BigDecimal totalAmount;

    private OrderStatus orderStatus;

    private List<OrderItemResponse> orderItems;

    private UUID userId;

    private LocalDateTime createdAt;


}