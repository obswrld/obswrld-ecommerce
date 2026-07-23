package com.observe.observe.dtos.response;

import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {

    private UUID id;

    private String street;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private String country;

    private Boolean isDefault;

    private UUID userId;
    
}