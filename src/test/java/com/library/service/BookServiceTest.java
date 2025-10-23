package com.library.service;

import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.repository.JdbcBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private JdbcBookRepository jdbcBookRepository;

    @Mock
    private Model model;

    @InjectMocks
    private BookService bookService;

    private User author;
    private User admin;
    private User otherUser;
    private Book book;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setRole(Role.USER);

        admin = new User();
        admin.setId(2L);
        admin.setRole(Role.ADMIN);

        otherUser = new User();
        otherUser.setId(3L);
        otherUser.setRole(Role.USER);

        book = new Book();
        book.setId(1L);
        book.setTitle("Тестовая книга");
        book.setAuthorId(1L);
    }

    @Test
    void createBook_shouldSetAuthorIdAndSave() {
        Book newBook = new Book();
        newBook.setTitle("Новая книга");

        Book result = bookService.createBook(newBook, author);

        assertEquals(author.getId(), result.getAuthorId());
        verify(jdbcBookRepository, times(1)).add(newBook);
    }

    @Test
    void updateBook_shouldCallRepositoryUpdate() {
        Book result = bookService.updateBook(book);

        assertEquals(book, result);
        verify(jdbcBookRepository, times(1)).update(book);
    }

    @Test
    void deleteBook_shouldCallRepositoryDelete() {
        Long bookId = 1L;

        bookService.deleteBook(bookId);

        verify(jdbcBookRepository, times(1)).delete(bookId);
    }

    @Test
    void getBookById_shouldReturnBook() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void getBookById_shouldReturnEmpty_whenNotFound() {
        when(jdbcBookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        List<Book> books = Arrays.asList(book, new Book());
        when(jdbcBookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(jdbcBookRepository, times(1)).findAll();
    }

    @Test
    void getBookByAuthor_shouldReturnAuthorBooks() {
        List<Book> books = Arrays.asList(book);
        when(jdbcBookRepository.findByAuthorId(1L)).thenReturn(books);

        List<Book> result = bookService.getBookByAuthor(1L);

        assertEquals(1, result.size());
        assertEquals(book, result.get(0));
    }

    @Test
    void bookExist_shouldReturnTrue_whenBookExists() {
        when(jdbcBookRepository.existsById(1L)).thenReturn(true);

        boolean result = bookService.bookExist(book);

        assertTrue(result);
    }

    @Test
    void canEditBook_shouldReturnTrue_whenUserIsAuthor() {
        boolean result = bookService.canEditBook(author, book);

        assertTrue(result);
    }

    @Test
    void canEditBook_shouldReturnTrue_whenUserIsAdmin() {
        boolean result = bookService.canEditBook(admin, book);

        assertTrue(result);
    }

    @Test
    void canEditBook_shouldReturnFalse_whenUserIsNotAuthorOrAdmin() {
        boolean result = bookService.canEditBook(otherUser, book);

        assertFalse(result);
    }

    @Test
    void prepareEditForm_shouldReturnViewName_whenAuthorized() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        String result = bookService.prepareEditForm(1L, author, model);

        assertEquals("createBook", result);
        verify(model, times(1)).addAttribute("book", book);
    }

    @Test
    void prepareEditForm_shouldRedirect_whenNotAuthorized() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        String result = bookService.prepareEditForm(1L, otherUser, model);

        assertEquals("redirect:/books", result);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void prepareEditForm_shouldRedirect_whenBookNotFound() {
        when(jdbcBookRepository.findById(999L)).thenReturn(Optional.empty());

        String result = bookService.prepareEditForm(999L, author, model);

        assertEquals("redirect:/books", result);
    }

    @Test
    void updateBookIfAuthorized_shouldReturnTrue_whenAuthorized() {
        Book updatedBook = new Book();
        updatedBook.setTitle("Обновленное название");

        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.updateBookIfAuthorized(1L, updatedBook, author);

        assertTrue(result);
        assertEquals(1L, updatedBook.getId());
        assertEquals(1L, updatedBook.getAuthorId());
        verify(jdbcBookRepository, times(1)).update(updatedBook);
    }

    @Test
    void updateBookIfAuthorized_shouldReturnFalse_whenNotAuthorized() {
        Book updatedBook = new Book();
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.updateBookIfAuthorized(1L, updatedBook, otherUser);

        assertFalse(result);
        verify(jdbcBookRepository, never()).update(any());
    }

    @Test
    void updateBookIfAuthorized_shouldReturnFalse_whenBookNotFound() {
        Book updatedBook = new Book();
        when(jdbcBookRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = bookService.updateBookIfAuthorized(999L, updatedBook, author);

        assertFalse(result);
        verify(jdbcBookRepository, never()).update(any());
    }

    @Test
    void deleteBookIfAuthorized_shouldReturnTrue_whenAuthorized() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBookIfAuthorized(1L, author);

        assertTrue(result);
        verify(jdbcBookRepository, times(1)).delete(1L);
    }

    @Test
    void deleteBookIfAuthorized_shouldReturnTrue_whenAdmin() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBookIfAuthorized(1L, admin);

        assertTrue(result);
        verify(jdbcBookRepository, times(1)).delete(1L);
    }

    @Test
    void deleteBookIfAuthorized_shouldReturnFalse_whenNotAuthorized() {
        when(jdbcBookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.deleteBookIfAuthorized(1L, otherUser);

        assertFalse(result);
        verify(jdbcBookRepository, never()).delete(any());
    }

    @Test
    void deleteBookIfAuthorized_shouldReturnFalse_whenBookNotFound() {
        when(jdbcBookRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = bookService.deleteBookIfAuthorized(999L, author);

        assertFalse(result);
        verify(jdbcBookRepository, never()).delete(any());
    }
}