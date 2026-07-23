package com.observe.observe.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import com.observe.observe.models.Role;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse {

    private UUID id;

    private String email;

    private String fullName;

    private String phoneNumber;

    private Role role;

    @JsonProperty("isActive")
    private boolean isActive;

    private LocalDateTime createdAt;
}
