package com.library.repository;

import com.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book add(Book book);
    Book update(Book book);
    void delete(Long id);
    List<Book> findByAuthorId(Long authorId);
    boolean existsById(Long id);
}
