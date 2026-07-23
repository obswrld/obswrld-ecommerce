package com.observe.observe.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private UUID id;

    private UUID userId;

    private String userName;
    
    private UUID productId;

    private String productName;

    private Integer rating;
    
    private String comment;

    private LocalDateTime createdAt;

}