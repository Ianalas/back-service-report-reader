package com.example.backservicereportreader.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AiResultDTO {
    private UUID id;
    private String content;
    private UUID userId;
    private LocalDateTime createdAt;
    private String title;

    public AiResultDTO() {}

    public AiResultDTO(UUID id, String content, UUID userId, LocalDateTime createdAt, String title) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
        this.title = title;
    }

}
