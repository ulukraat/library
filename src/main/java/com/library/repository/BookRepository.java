package com.library.repository;

import com.library.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getLong("author_id"),
                    rs.getString("image")
            );
            book.setId(rs.getLong("id"));
            return book;
        }
    }

    public List<Book> bookFindAll() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    public Book bookFindById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
    }

    public void bookAdd(Book book) {
        String sql = "INSERT INTO books (title, description, author_id, image) VALUES (?, ?, ? ,?)";
        jdbcTemplate.update(sql, book.getTitle(), book.getDescription(), book.getAuthorId() , book.getImage());
    }

    public void bookUpdate(Book book) {
        String sql = "UPDATE books SET title = ?, description = ?, author_id = ? ,image = ? WHERE id = ?";
        jdbcTemplate.update(sql, book.getTitle(), book.getDescription(), book.getAuthorId(), book.getImage(), book.getId());
    }

    public void bookDelete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Book> bookFindByAuthorId(Long authorId) {
        String sql = "SELECT * FROM books WHERE author_id = ?";
        return jdbcTemplate.query(sql, new BookRowMapper(), authorId);
    }

    public boolean bookExistsById(Long id) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
