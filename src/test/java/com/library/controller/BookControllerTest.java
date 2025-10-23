package com.library.controller;

import com.library.config.SecurityConfig;
import com.library.model.Book;
import com.library.model.Role;
import com.library.model.User;
import com.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {BookController.class, SecurityConfig.class})
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void listBooks_shouldReturnBooksView_forAnyAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/books")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("books"));
    }

    @Test
    void showCreateForm_shouldReturnCreateBookView_forAuthor() throws Exception {
        mockMvc.perform(get("/books/create")
                        .with(user(mockUserWithRole("AUTHOR"))))
                .andExpect(status().isOk())
                .andExpect(view().name("createBook"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void createBook_shouldRedirectToBooks_forAuthor() throws Exception {
        mockMvc.perform(post("/books/create")
                        .with(csrf())
                        .with(user(mockUserWithRole("AUTHOR")))
                        .param("title", "Test Book")
                        .param("description", "Test Description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        Mockito.verify(bookService).createBook(Mockito.any(Book.class), Mockito.any(User.class));
    }

    @Test
    void createBook_shouldReturnForbiddenForUserRole() throws Exception {
        mockMvc.perform(get("/books/create")
                        .with(user(mockUserWithRole("USER"))))
                .andExpect(status().isForbidden());
    }

    private User mockUserWithRole(String roleName) {
        User user = new User();
        user.setId(1L);
        user.setLogin(roleName.toLowerCase());
        user.setPassword("password");

        Role role = Role.valueOf(roleName.toUpperCase());
        user.setRole(role);

        return user;
    }
}
