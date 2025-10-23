package com.library.repository;

import com.library.model.Role;
import com.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JdbcUserRepositoryTest {

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM books");
        jdbcTemplate.execute("DELETE FROM users");


        user1 = User.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .login("ivan")
                .password("password123")
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .firstName("Петр")
                .lastName("Петров")
                .login("petr")
                .password("password456")
                .role(Role.ADMIN)
                .build();

        user1 = userRepository.add(user1);
        user2 = userRepository.add(user2);

        assertNotNull(user1.getId(), "User1 ID should not be null after add");
        assertNotNull(user2.getId(), "User2 ID should not be null after add");
    }

    @Test
    void testAddUser() {
        User newUser = User.builder()
                .firstName("Сидор")
                .lastName("Сидоров")
                .login("sidor")
                .password("password789")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.add(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("Сидор", savedUser.getFirstName());
        assertEquals("Сидоров", savedUser.getLastName());
        assertEquals("sidor", savedUser.getLogin());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals("ivan")));
        assertTrue(users.stream().anyMatch(u -> u.getLogin().equals("petr")));
    }

    @Test
    void testFindById() {
        Optional<User> found = userRepository.findById(user1.getId());

        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getFirstName());
        assertEquals("ivan", found.get().getLogin());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> notFound = userRepository.findById(999L);
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testFindByLogin() {
        Optional<User> found = userRepository.findByLogin("ivan");

        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getFirstName());
        assertEquals(user1.getId(), found.get().getId());
    }

    @Test
    void testFindByLoginNotFound() {
        Optional<User> notFound = userRepository.findByLogin("nonexistent");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testUpdateUser() {
        user1.setFirstName("Иван Обновленный");
        user1.setLastName("Иванов Новый");
        user1.setRole(Role.ADMIN);

        User updated = userRepository.update(user1);

        assertEquals("Иван Обновленный", updated.getFirstName());
        assertEquals("Иванов Новый", updated.getLastName());
        assertEquals(Role.ADMIN, updated.getRole());

        // Проверяем, что изменения сохранились в БД
        Optional<User> fromDb = userRepository.findById(user1.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Иван Обновленный", fromDb.get().getFirstName());
        assertEquals(Role.ADMIN, fromDb.get().getRole());
    }

    @Test
    void testUpdateNonExistentUser() {
        User nonExistent = User.builder()
                .id(999L)
                .firstName("Test")
                .lastName("Test")
                .login("test")
                .password("test")
                .role(Role.USER)
                .build();

        assertThrows(RuntimeException.class, () -> userRepository.update(nonExistent));
    }

    @Test
    void testDeleteUser() {
        Long idToDelete = user1.getId();

        userRepository.delete(idToDelete);

        Optional<User> deleted = userRepository.findById(idToDelete);
        assertTrue(deleted.isEmpty());

        // Проверяем, что остался только один пользователь
        List<User> remaining = userRepository.findAll();
        assertEquals(1, remaining.size());
        assertEquals("petr", remaining.get(0).getLogin());
    }

    @Test
    void testDeleteNonExistentUser() {
        assertThrows(RuntimeException.class, () -> userRepository.delete(999L));
    }

    @Test
    void testExistsById() {
        assertTrue(userRepository.existsById(user1.getId()));
        assertTrue(userRepository.existsById(user2.getId()));
        assertFalse(userRepository.existsById(999L));
    }

    @Test
    void testExistsByLogin() {
        assertTrue(userRepository.existsByLogin("ivan"));
        assertTrue(userRepository.existsByLogin("petr"));
        assertFalse(userRepository.existsByLogin("nonexistent"));
    }

    @Test
    void testAddMultipleUsersWithDifferentRoles() {
        User admin = User.builder()
                .firstName("Админ")
                .lastName("Админов")
                .login("admin")
                .password("admin123")
                .role(Role.ADMIN)
                .build();

        User regularUser = User.builder()
                .firstName("Юзер")
                .lastName("Юзеров")
                .login("user")
                .password("user123")
                .role(Role.USER)
                .build();

        admin = userRepository.add(admin);
        regularUser = userRepository.add(regularUser);

        assertNotNull(admin.getId());
        assertNotNull(regularUser.getId());
        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.USER, regularUser.getRole());

        List<User> allUsers = userRepository.findAll();
        assertEquals(4, allUsers.size());
    }

    @Test
    void testUpdatePassword() {
        String newPassword = "newSecurePassword123";
        user1.setPassword(newPassword);

        userRepository.update(user1);

        Optional<User> updated = userRepository.findById(user1.getId());
        assertTrue(updated.isPresent());
        assertEquals(newPassword, updated.get().getPassword());
    }

}