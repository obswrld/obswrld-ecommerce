package com.observe.observe.dtos.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String username;
    
    private String fullName;

    private String phoneNumber;
}
