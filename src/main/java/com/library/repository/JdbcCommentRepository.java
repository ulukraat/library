package com.library.repository;

import com.library.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbc;

    private static final String SELECT_FIELDS =
            "id, user_id, book_id, text, created_at";

    @Override
    public Comment add(Comment comment) {
        String sql = """
                INSERT INTO comments (user_id, book_id, text, created_at)
                VALUES (?, ?, ?, ?)
                """;
        jdbc.update(sql,
                comment.getUserId(),
                comment.getBookId(),
                comment.getText(),
                comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now()
        );
        return comment;
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM comments";
        return jdbc.query(sql, new CommentRowMapper());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM comments WHERE id = ?";
        try {
            Comment comment = jdbc.queryForObject(sql, new CommentRowMapper(), id);
            return Optional.ofNullable(comment);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM comments WHERE book_id = ?";
        return jdbc.query(sql, new CommentRowMapper(), bookId);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM comments WHERE user_id = ?";
        return jdbc.query(sql, new CommentRowMapper(), userId);
    }

    @Override
    public Comment update(Comment comment) {
        String sql = """
                UPDATE comments
                SET text = ?, created_at = ?
                WHERE id = ?
                """;
        jdbc.update(sql,
                comment.getText(),
                comment.getCreatedAt(),
                comment.getId());
        return comment;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM comments WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Comment.builder()
                    .id(rs.getLong("id"))
                    .userId(rs.getLong("user_id"))
                    .bookId(rs.getLong("book_id"))
                    .text(rs.getString("text"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
        }
    }
}
