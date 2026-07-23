package com.observe.observe.dtos.response;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private UUID id;
    
    private List<CartItemResponse> items;

    private UUID userId;

    
}