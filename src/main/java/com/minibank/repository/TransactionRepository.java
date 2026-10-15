package com.minibank.repository;

import com.minibank.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> new Transaction(
            rs.getLong("id"),
            rs.getLong("from_account_id"),
            rs.getLong("to_account_id"),
            rs.getBigDecimal("amount"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    public Transaction save(Long fromAccountId, Long toAccountId, BigDecimal amount, String status) {
        String sql = "INSERT INTO transactions (from_account_id, to_account_id, amount, status) " +
                     "VALUES (?, ?, ?, ?) RETURNING id, from_account_id, to_account_id, amount, status, created_at";
        return jdbcTemplate.queryForObject(sql, transactionRowMapper, fromAccountId, toAccountId, amount, status);
    }

    public List<Transaction> findByAccountId(Long accountId) {
        String sql = "SELECT id, from_account_id, to_account_id, amount, status, created_at " +
                     "FROM transactions WHERE from_account_id = ? OR to_account_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, accountId, accountId);
    }
}