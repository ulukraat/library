package com.library.service;

import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.JdbcBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final JdbcBookRepository jdbcBookRepository;

    public Book createBook(Book book,User user) {
        book.setAuthorId(user.getId());
        jdbcBookRepository.add(book);
        return book;
    }
    public Book updateBook(Book book) {
        jdbcBookRepository.update(book);
        return book;
    }
    public void deleteBook(Long id ) {
        jdbcBookRepository.delete(id);
    }
    public Optional<Book> getBookById(Long id) {
        return jdbcBookRepository.findById(id);
    }
    public List<Book> getAllBooks() {
        return jdbcBookRepository.findAll();
    }
    public List<Book> getBookByAuthor(Long id) {
        return jdbcBookRepository.findByAuthorId(id);
    }
    public boolean bookExist(Book book) {
        return jdbcBookRepository.existsById(book.getId());
    }
    public boolean canEditBook(User user, Book book) {
        return user.getRole() == Role.ADMIN || book.getAuthorId().equals(user.getId());
    }
    public String prepareEditForm(Long bookId, User user, Model model) {
        return getBookById(bookId)
                .filter(book -> canEditBook(user,book))
                .map(book -> {
                    model.addAttribute("book", book);
                    return "createBook";
                })
                .orElse("redirect:/books");
    }
    public boolean updateBookIfAuthorized(Long id, Book bookFromForm, User user) {
        return getBookById(id)
                .filter(existing -> canEditBook(user, existing))
                .map(existing -> {
                    bookFromForm.setId(existing.getId());
                    bookFromForm.setAuthorId(existing.getAuthorId());
                    jdbcBookRepository.update(bookFromForm);
                    return true;
                })
                .orElse(false);
    }
    public boolean deleteBookIfAuthorized(Long id, User user) {
        return getBookById(id)
                .filter(book -> canEditBook(user, book))
                .map(book -> {
                    jdbcBookRepository.delete(id);
                    return true;
                })
                .orElse(false);
    }

}
