package com.minibank.repository;

import com.minibank.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public User save(String fullName, String email) {
        String sql = "INSERT INTO users (full_name, email) VALUES (?, ?) RETURNING id, full_name, email, created_at";
        return jdbcTemplate.queryForObject(sql, userRowMapper, fullName, email);
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, full_name, email, created_at FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.stream().findFirst();
    }

    public List<User> findAll() {
        String sql = "SELECT id, full_name, email, created_at FROM users ORDER BY id";
        return jdbcTemplate.query(sql, userRowMapper);
    }
}