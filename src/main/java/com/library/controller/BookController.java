package com.library.controller;

import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {
    @Autowired
    private final BookService bookService;
    BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/books/create")
    public String createBook(Model model) {
        model.addAttribute("book", new Book());
        return "createBook";
    }
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @PostMapping("/books/create")
    public String createBook(@ModelAttribute("book") Book book,@AuthenticationPrincipal User user) {
        book.setAuthorId(user.getId());
        bookService.createBook(book);
        return "redirect:/books";
    }
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @PostMapping("/books/edit/{id}")
    public String editBook(@PathVariable("id") Long id,
                           @ModelAttribute("book") Book bookFromForm,
                           @AuthenticationPrincipal User user) {

        Book existingBook = bookService.getBookById(id);

        if (user.getRole() == Role.AUTHOR && !existingBook.getAuthorId().equals(user.getId())) {
            return "redirect:/books";
        }

        bookFromForm.setId(existingBook.getId());
        bookFromForm.setAuthorId(existingBook.getAuthorId());

        bookService.updateBook(bookFromForm);
        return "redirect:/books";
    }
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal User user) {
        Book existingBook = bookService.getBookById(id);

        if (user.getRole() == Role.AUTHOR && !existingBook.getAuthorId().equals(user.getId())) {
            return "redirect:/books";
        }

        model.addAttribute("book", existingBook);
        return "createBook";
    }

}
