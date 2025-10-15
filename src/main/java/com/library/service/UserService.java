package com.library.service;

import com.library.model.User;
import com.library.repository.JdbcUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final JdbcUserRepository jdbcUserRepository;
    private final PasswordEncoder passwordEncoder;

    public User updateUser(User user) {
        jdbcUserRepository.update(user);
        return user;
    }
    public void deleteUser(User user) {
        jdbcUserRepository.delete(user.getId());
    }
    public Optional<User> getUserById(Long id) {
       return jdbcUserRepository.findById(id);
    }
    public List<User> getUsers() {
        return jdbcUserRepository.findAll();
    }
    public boolean userExistsByLogin(String username) {
        return jdbcUserRepository.existsByLogin(username);
    }
    public boolean userExistsById(Long id) {
        return jdbcUserRepository.existsById(id);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        jdbcUserRepository.add(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return jdbcUserRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
