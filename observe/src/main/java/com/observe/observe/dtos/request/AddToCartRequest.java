package com.observe.observe.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}