package com.library.repository;

import com.library.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment(
                    rs.getLong("user_id"),
                    rs.getLong("book_id"),
                    rs.getString("text"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
            comment.setId(rs.getLong("id"));
            return comment;
        }
    }

    public void commentAdd(Comment comment) {
        String sql = "INSERT INTO comments (user_id, book_id, text, created_at) VALUES (?, ?, ?, ?)";
        jdbc.update(sql,
                comment.getUserId(),
                comment.getBookId(),
                comment.getText(),
                comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now()
        );
    }

    public List<Comment> commentFindAll() {
        String sql = "SELECT * FROM comments";
        return jdbc.query(sql, new CommentRowMapper());
    }

    public Comment commentFindById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        return jdbc.queryForObject(sql, new CommentRowMapper(), id);
    }

    public List<Comment> commentFindByBookId(Long bookId) {
        String sql = "SELECT * FROM comments WHERE book_id = ?";
        return jdbc.query(sql, new CommentRowMapper(), bookId);
    }

    public List<Comment> commentFindByUserId(Long userId) {
        String sql = "SELECT * FROM comments WHERE user_id = ?";
        return jdbc.query(sql, new CommentRowMapper(), userId);
    }

    public void commentDelete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void commentUpdate(Comment comment) {
        String sql = "UPDATE comments SET text = ?, created_at = ? WHERE id = ?";
        jdbc.update(sql, comment.getText(), comment.getCreatedAt(), comment.getId());
    }

    public boolean commentExistsById(Long id) {
        String sql = "SELECT COUNT(*) FROM comments WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
