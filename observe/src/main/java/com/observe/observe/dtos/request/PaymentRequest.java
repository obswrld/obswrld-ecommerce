package com.observe.observe.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import com.observe.observe.models.PaymentMethod;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private PaymentMethod method;
    
}