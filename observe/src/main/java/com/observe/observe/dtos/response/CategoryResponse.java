package com.observe.observe.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private UUID id;

    private String name;

    private String description;

    private String slug;

    private UUID parentId;

    private LocalDateTime createdAt;

}