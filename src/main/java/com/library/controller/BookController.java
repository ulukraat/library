package com.library.controller;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "createBook";
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") Book book,
                             @AuthenticationPrincipal User user) {
        bookService.createBook(book,user);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               @AuthenticationPrincipal User user) {
        return bookService.prepareEditForm(id,user,model);
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id,
                           @ModelAttribute("book") Book bookFromForm,
                           @AuthenticationPrincipal User user) {
        boolean updated = bookService.updateBookIfAuthorized(id, bookFromForm, user);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id,
                             @AuthenticationPrincipal User user) {
        bookService.deleteBookIfAuthorized(id, user);
        return "redirect:/books";
    }
}
