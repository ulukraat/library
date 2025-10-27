package com.library.repository;

import com.library.model.Role;
import com.library.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbc;

    private static final String SELECT_FIELDS =
            "id, first_name, last_name, login, password, role";

    @Override
    public User add(User user) {
        String sql = """
        INSERT INTO users (first_name, last_name, login, password, role)
        VALUES (?, ?, ?, ?, ?)
        RETURNING id
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT " + SELECT_FIELDS + " FROM users";
        return jdbc.query(sql, new UserRowMapper());
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT " + SELECT_FIELDS + " FROM users WHERE login = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), login);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE users
                SET first_name = ?, last_name = ?, login = ?, password = ?, role = ?
                WHERE id = ?
                """;
        int rowsAffected = jdbc.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getLogin(),
                user.getPassword(),
                user.getRole().name(),
                user.getId());

        if (rowsAffected == 0) {
            throw new RuntimeException("User with id " + user.getId() + " not found");
        }

        return user;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbc.update(sql, id);

        if (rowsAffected == 0) {
            throw new RuntimeException("User with id " + id + " not found");
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, login);
        return count != null && count > 0;
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getLong("id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .login(rs.getString("login"))
                    .password(rs.getString("password"))
                    .role(Role.valueOf(rs.getString("role")))
                    .build();
        }
    }
}