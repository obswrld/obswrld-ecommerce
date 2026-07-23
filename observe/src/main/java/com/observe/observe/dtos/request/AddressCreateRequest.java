package com.observe.observe.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateRequest {

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String country;

    private Boolean isDefault;
}