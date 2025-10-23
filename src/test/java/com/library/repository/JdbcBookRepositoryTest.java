package com.library.repository;

import com.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class JdbcBookRepositoryTest {

    @Autowired
    private JdbcBookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM books");

        book1 = Book.builder()
                .title("Book One")
                .description("First book")
                .authorId(1L)
                .image("image1.png")
                .build();

        book2 = Book.builder()
                .title("Book Two")
                .description("Second book")
                .authorId(2L)
                .image("image2.png")
                .build();

        book1 = bookRepository.add(book1);
        book2 = bookRepository.add(book2);

        assertNotNull(book1.getId(), "Book1 ID should not be null after add");
        assertNotNull(book2.getId(), "Book2 ID should not be null after add");
    }

    @Test
    void testAddAndFindById() {
        Book book = Book.builder()
                .title("Book Three")
                .description("Third book")
                .authorId(3L)
                .image("image3.png")
                .build();

        book = bookRepository.add(book);

        assertNotNull(book.getId(), "Book ID should not be null after add");

        List<Book> allBooks = bookRepository.findAll();
        assertEquals(3, allBooks.size());
        assertTrue(allBooks.stream().anyMatch(b -> b.getTitle().equals("Book Three")));

        Optional<Book> found = bookRepository.findById(book.getId());
        assertTrue(found.isPresent());
        assertEquals("Book Three", found.get().getTitle());
    }

    @Test
    void testFindAll() {
        List<Book> books = bookRepository.findAll();
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Book One")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Book Two")));
    }

    @Test
    void testUpdate() {
        book1.setTitle("Updated Title");
        book1.setDescription("Updated Description");

        bookRepository.update(book1);

        Optional<Book> updated = bookRepository.findById(book1.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Title", updated.get().getTitle());
        assertEquals("Updated Description", updated.get().getDescription());
    }

    @Test
    void testDelete() {
        Long idToDelete = book1.getId();

        bookRepository.delete(idToDelete);

        Optional<Book> deleted = bookRepository.findById(idToDelete);
        assertTrue(deleted.isEmpty());

        List<Book> remaining = bookRepository.findAll();
        assertEquals(1, remaining.size());
        assertEquals("Book Two", remaining.get(0).getTitle());
    }

    @Test
    void testFindByAuthorId() {
        List<Book> books = bookRepository.findByAuthorId(1L);
        assertEquals(1, books.size());
        assertEquals("Book One", books.get(0).getTitle());
    }

    @Test
    void testExistsById() {
        assertTrue(bookRepository.existsById(book1.getId()));
        assertFalse(bookRepository.existsById(999L));
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Book> notFound = bookRepository.findById(999L);
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testAddMultipleBooksWithSameAuthor() {
        Book book3 = Book.builder()
                .title("Book Three")
                .description("Third book")
                .authorId(1L)
                .image("image3.png")
                .build();

        book3 = bookRepository.add(book3);

        List<Book> authorBooks = bookRepository.findByAuthorId(1L);
        assertEquals(2, authorBooks.size());
    }
}