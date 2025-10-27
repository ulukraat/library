package com.library.repository;

import com.library.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_FIELDS =
            "id, author_id, title, description, image";

    public List<Book> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM books WHERE id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Book add(Book book) {
        String sql = """
            INSERT INTO books (title, description, author_id, image)
            VALUES (?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getDescription());
            ps.setLong(3, book.getAuthorId());
            ps.setString(4, book.getImage());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {
            Object idValue = keys.values().iterator().next();
            book.setId(((Number) idValue).longValue());
        }

        return book;
    }

    public Book update(Book book) {
        String sql = """
                UPDATE books
                SET title = ?, description = ?, author_id = ?, image = ?
                WHERE id = ?
                """;

        int rowsAffected = jdbcTemplate.update(sql,
                book.getTitle(),
                book.getDescription(),
                book.getAuthorId(),
                book.getImage(),
                book.getId());

        if (rowsAffected == 0) {
            throw new RuntimeException("Book with id " + book.getId() + " not found");
        }

        return book;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new RuntimeException("Book with id " + id + " not found");
        }
    }

    public List<Book> findByAuthorId(Long authorId) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM books WHERE author_id = ?";
        return jdbcTemplate.query(sql, new BookRowMapper(), authorId);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Book.builder()
                    .id(rs.getLong("id"))
                    .authorId(rs.getLong("author_id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .image(rs.getString("image"))
                    .build();
        }
    }
}