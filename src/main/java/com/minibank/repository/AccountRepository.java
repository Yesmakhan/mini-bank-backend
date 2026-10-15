package com.minibank.repository;

import com.minibank.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> new Account(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getBigDecimal("balance"),
            rs.getString("currency"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public Account createAccount(Long userId, BigDecimal initialBalance, String currency) {
        String sql = "INSERT INTO accounts (user_id, balance, currency) VALUES (?, ?, ?) " +
                     "RETURNING id, user_id, balance, currency, created_at";
        return jdbcTemplate.queryForObject(sql, accountRowMapper, userId, initialBalance, currency);
    }

    public Optional<Account> findById(Long id) {
        String sql = "SELECT id, user_id, balance, currency, created_at FROM accounts WHERE id = ?";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, id);
        return accounts.stream().findFirst();
    }

    public Optional<Account> findByIdForUpdate(Long id) {
        String sql = "SELECT id, user_id, balance, currency, created_at FROM accounts WHERE id = ? FOR UPDATE";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, id);
        return accounts.stream().findFirst();
    }

    public List<Account> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, balance, currency, created_at FROM accounts WHERE user_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, accountRowMapper, userId);
    }

    public void updateBalance(Long accountId, BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
    }
}