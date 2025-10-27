package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    private Long userId;
    private Long bookId;
    private String text;
    private LocalDateTime createdAt;
}
