package com.library.service;

import com.library.model.Role;
import com.library.model.User;
import com.library.repository.JdbcUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JdbcUserRepository jdbcUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setLogin("ivan");
        user.setPassword("password123");
        user.setRole(Role.USER);
    }

    @Test
    void updateUser_shouldCallRepositoryUpdate() {
        User result = userService.updateUser(user);

        assertEquals(user, result);
        verify(jdbcUserRepository, times(1)).update(user);
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        userService.deleteUser(user);

        verify(jdbcUserRepository, times(1)).delete(user.getId());
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        when(jdbcUserRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(jdbcUserRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldReturnEmpty_whenUserNotFound() {
        when(jdbcUserRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(jdbcUserRepository, times(1)).findById(999L);
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setLogin("petr");

        List<User> users = Arrays.asList(user, user2);
        when(jdbcUserRepository.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(jdbcUserRepository, times(1)).findAll();
    }

    @Test
    void userExistsByLogin_shouldReturnTrue_whenUserExists() {
        when(jdbcUserRepository.existsByLogin("ivan")).thenReturn(true);

        boolean result = userService.userExistsByLogin("ivan");

        assertTrue(result);
        verify(jdbcUserRepository, times(1)).existsByLogin("ivan");
    }

    @Test
    void userExistsByLogin_shouldReturnFalse_whenUserNotExists() {
        when(jdbcUserRepository.existsByLogin("nonexistent")).thenReturn(false);

        boolean result = userService.userExistsByLogin("nonexistent");

        assertFalse(result);
        verify(jdbcUserRepository, times(1)).existsByLogin("nonexistent");
    }

    @Test
    void userExistsById_shouldReturnTrue_whenUserExists() {
        when(jdbcUserRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.userExistsById(1L);

        assertTrue(result);
        verify(jdbcUserRepository, times(1)).existsById(1L);
    }

    @Test
    void userExistsById_shouldReturnFalse_whenUserNotExists() {
        when(jdbcUserRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.userExistsById(999L);

        assertFalse(result);
        verify(jdbcUserRepository, times(1)).existsById(999L);
    }

    @Test
    void registerUser_shouldEncodePasswordAndSaveUser() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setPassword(rawPassword);
        newUser.setRole(Role.USER);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        User result = userService.registerUser(newUser);

        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(jdbcUserRepository, times(1)).add(newUser);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        when(jdbcUserRepository.findByLogin("ivan")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("ivan");

        assertNotNull(result);
        assertEquals("ivan", result.getUsername());
        assertEquals(user, result);
        verify(jdbcUserRepository, times(1)).findByLogin("ivan");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(jdbcUserRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));

        verify(jdbcUserRepository, times(1)).findByLogin("nonexistent");
    }

    @Test
    void registerUser_shouldSetEncodedPasswordBeforeSaving() {
        User newUser = new User();
        newUser.setLogin("testuser");
        newUser.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("$2a$10$encodedHash");

        userService.registerUser(newUser);

        assertEquals("$2a$10$encodedHash", newUser.getPassword());
        verify(jdbcUserRepository, times(1)).add(newUser);
    }

    @Test
    void deleteUser_shouldDeleteByCorrectId() {
        User userToDelete = new User();
        userToDelete.setId(5L);
        userToDelete.setLogin("deleteme");

        userService.deleteUser(userToDelete);

        verify(jdbcUserRepository, times(1)).delete(5L);
    }

    @Test
    void getUsers_shouldReturnEmptyList_whenNoUsers() {
        when(jdbcUserRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getUsers();

        assertTrue(result.isEmpty());
        verify(jdbcUserRepository, times(1)).findAll();
    }
}