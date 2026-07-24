package com.observe.observe.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.observe.observe.models.PaymentStatus;
import com.observe.observe.models.PaymentMethod;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private UUID id;

    private UUID orderId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    
    private PaymentStatus status;

    private String transactionReference;

    private LocalDateTime createdAt;
    
}