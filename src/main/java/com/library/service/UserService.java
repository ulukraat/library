package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public void createUser(User user) {
        userRepository.userAdd(user);
    }
    public void updateUser(User user) {
        userRepository.userUpdate(user);
    }
    public void deleteUser(User user) {
        userRepository.userDelete(user.getId());
    }
    public User getUserById(Long id) {
       return userRepository.userFindById(id);
    }
    public List<User> getUsers() {
        return userRepository.userFindAll();
    }
    public boolean userExistsByLogin(String username) {
        return userRepository.userExistsByLogin(username);
    }
    public boolean userExistsById(Long id) {
        return userRepository.userExistsById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.userFindByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
