package com.observe.observe.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    private String description;

    @NotBlank(message = "Slug is required")
    @Size(max = 100, message = "Slug must be less than 100 characters")
    private String slug;
    
    private UUID parentId;
    
}
