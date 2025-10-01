package com.library.repository;

import com.library.model.Role;
import com.library.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = Role.valueOf(rs.getString("role"));
            User user = new User(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("login"),
                    rs.getString("password"),
                    role
            );
            user.setId(rs.getLong("id"));
            return user;
        }
    }

    public void userAdd(User user) {
        String sql = "INSERT INTO users (first_name, last_name, login, password,role) VALUES (?, ?, ?, ?,?)";
        jdbc.update(sql, user.getFirstName(), user.getLastName(), user.getLogin(), user.getPassword(), user.getRole().name());
    }

    public List<User> userFindAll() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, new UserRowMapper());
    }

    public User userFindById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.queryForObject(sql, new UserRowMapper(), id);
    }

    public void userUpdate(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, login = ?, password = ? , role = ? WHERE id = ?";
        jdbc.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getLogin(),
                user.getPassword(),
                user.getRole().name(),
                user.getId()
        );
    }

    public void userDelete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbc.update(sql, id);
    }

    public boolean userExistsById(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public boolean userExistsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, login);
        return count != null && count > 0;
    }
    public Optional<User> userFindByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), login);
            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
