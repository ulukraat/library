package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public void createBook(Book book) {
        bookRepository.bookAdd(book);
    }
    public void updateBook(Book book) {
        bookRepository.bookUpdate(book);
    }
    public void deleteBook(Long id ) {
        bookRepository.bookDelete(id);
    }
    public Book getBookById(Long id) {
        return bookRepository.bookFindById(id);
    }
    public List<Book> getAllBooks() {
        return bookRepository.bookFindAll();
    }
    public List<Book> getBookByAuthor(Long id) {
        return bookRepository.bookFindByAuthorId(id);
    }
    public boolean bookExist(Book book) {
        return bookRepository.bookExistsById(book.getId());
    }
}
