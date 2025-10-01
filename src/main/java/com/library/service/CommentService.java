package com.library.service;

import com.library.model.Comment;
import com.library.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void createComment(Comment comment) {
        commentRepository.commentAdd(comment);
    }
    public void updateComment(Comment comment) {
        commentRepository.commentUpdate(comment);
    }
    public void deleteComment(Comment comment) {
        commentRepository.commentDelete(comment.getId());
    }
    public List<Comment> getComments() {
       return commentRepository.commentFindAll();
    }
    public Comment getCommentById(Long id) {
        return commentRepository.commentFindById(id);
    }
    public List<Comment> getCommentsByBookId(Long bookId) {
        return commentRepository.commentFindByBookId(bookId);
    }
    public List<Comment> getCommentsUserId(Long userId) {
        return  commentRepository.commentFindByUserId(userId);
    }
    public boolean commentExist(Comment comment) {
        return commentRepository.commentExistsById(comment.getId());
    }

}
