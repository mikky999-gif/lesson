package org.skypro.lesson.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasAnyTransactions(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
            """;

        return jdbcTemplate.queryForObject(sql, Integer.class, userId.toString(), productType) > 0;
    }

    public long calculateBalance(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE -t.amount END), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
            """;

        return jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
    }

    public long sumDepositsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = 'DEPOSIT'
            """;

        return jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
    }

    public long sumWithdrawalsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = 'WITHDRAWAL'
            """;

        return jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
    }

    public List<UUID> getAllUserIds() {
        String sql = "SELECT DISTINCT user_id FROM transactions";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> UUID.fromString(rs.getString("user_id"))
        );
    }

}

