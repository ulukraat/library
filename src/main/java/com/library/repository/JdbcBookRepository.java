package com.library.repository;

import com.library.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public Book add(Book book) {
        String sql = "INSERT INTO books (title, description, author_id, image) VALUES (?, ?, ? ,?)";
        jdbcTemplate.update(sql, book.getTitle(), book.getDescription(), book.getAuthorId() , book.getImage());
        return book;
    }

    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, description = ?, author_id = ? ,image = ? WHERE id = ?";
        jdbcTemplate.update(sql, book.getTitle(), book.getDescription(), book.getAuthorId(), book.getImage(), book.getId());
        return book;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Book> findByAuthorId(Long authorId) {
        String sql = "SELECT * FROM books WHERE author_id = ?";
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
            Book book = Book.builder()
                    .id(rs.getLong("id"))
                    .authorId(rs.getLong("author_id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .image(rs.getString("image"))
                    .build();
            book.setId(rs.getLong("id"));
            return book;
        }
    }
}
