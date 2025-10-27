package com.library.service;

import com.library.model.Comment;
import com.library.repository.JdbcCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final JdbcCommentRepository jdbcCommentRepository;

    public void createComment(Comment comment) {
        jdbcCommentRepository.add(comment);
    }
    public void updateComment(Comment comment) {
        jdbcCommentRepository.update(comment);
    }
    public void deleteComment(Comment comment) {
        jdbcCommentRepository.delete(comment.getId());
    }
    public List<Comment> getComments() {
       return jdbcCommentRepository.findAll();
    }
    public Optional<Comment> getCommentById(Long id) {
        return jdbcCommentRepository.findById(id);
    }
    public List<Comment> getCommentsByBookId(Long bookId) {
        return jdbcCommentRepository.findByBookId(bookId);
    }
    public List<Comment> getCommentsUserId(Long userId) {
        return  jdbcCommentRepository.findByUserId(userId);
    }
    public boolean commentExist(Comment comment) {
        return jdbcCommentRepository.existsById(comment.getId());
    }

}
