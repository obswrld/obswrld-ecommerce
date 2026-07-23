package com.observe.observe.dtos.request;

import com.observe.observe.models.ProductStatus;
import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {


    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must be less than 150 characters")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")    
    private BigDecimal price;

    private String description;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotBlank(message = "Slug is required")
    @Size(max = 150, message = "Slug must be less than 150 characters")
    private String slug;
}