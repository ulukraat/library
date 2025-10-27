package com.library.repository;

import com.library.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User add(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByLogin(String login);
    User update(User user);
    void delete(Long id);
    boolean existsById(Long id);
    boolean existsByLogin(String login);
}
