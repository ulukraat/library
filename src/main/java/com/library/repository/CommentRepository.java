package com.library.repository;

import com.library.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment add(Comment comment);
    List<Comment> findAll();
    Optional<Comment> findById(Long id);
    List<Comment> findByBookId(Long bookId);
    List<Comment> findByUserId(Long userId);
    Comment update(Comment comment);
    void delete(Long id);
    boolean existsById(Long id);
}
