package com.observe.observe.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}