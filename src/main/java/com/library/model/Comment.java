package com.library.model;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long userId;
    private Long bookId;
    private String text;
    private LocalDateTime createdAt;

    public Comment(Long userId, Long bookId, String text, LocalDateTime createdAt) {
        this.userId = userId;
        this.bookId = bookId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Comment(){}
}
