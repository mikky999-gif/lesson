package org.skypro.lesson.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final Cache<String, Object> cache;

    @Autowired
    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(10))
                .maximumSize(10_000)
                .build();
    }

    public boolean hasAnyTransactions(UUID userId, String productType) {
        String key = buildCacheKey("hasAnyTransactions", userId, productType);
        return (boolean) cache.get(key, k -> doHasAnyTransactions(userId, productType));
    }

    private boolean doHasAnyTransactions(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM transactions t
                 JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ? AND p.type = ?
            """.strip();
        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId.toString(), productType);
        return result != null ? result.booleanValue() : false;
    }

    public long countTransactionsByType(UUID userId, String productType) {
        String key = buildCacheKey("countTransactionsByType", userId, productType);
        return (long) cache.get(key, k -> doCountTransactionsByType(userId, productType));
    }

    private long doCountTransactionsByType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*)
            FROM transactions t JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ?
            """.strip();
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
        return result != null ? result.longValue() : 0;
    }

    public long calculateBalance(UUID userId, String productType) {
        String key = buildCacheKey("calculateBalance", userId, productType);
        return (long) cache.get(key, k -> doCalculateBalance(userId, productType));
    }

    private long doCalculateBalance(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE -t.amount END), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
            """.strip();
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
        return result != null ? result.longValue() : 0;
    }

    public long sumDepositsByType(UUID userId, String productType) {
        String key = buildCacheKey("sumDepositsByType", userId, productType);
        return (long) cache.get(key, k -> doSumDepositsByType(userId, productType));
    }

    private long doSumDepositsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = 'DEPOSIT'
            """.strip();
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
        return result != null ? result.longValue() : 0;
    }

    public long sumWithdrawalsByType(UUID userId, String productType) {
        String key = buildCacheKey("sumWithdrawalsByType", userId, productType);
        return (long) cache.get(key, k -> doSumWithdrawalsByType(userId, productType));
    }

    private long doSumWithdrawalsByType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions t
                     JOIN products p ON t.product_id = p.id
            WHERE t.user_id = ?
              AND p.type = ?
              AND t.type = 'WITHDRAWAL'
            """.strip();
        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId.toString(), productType);
        return result != null ? result.longValue() : 0;
    }

    private String buildCacheKey(String methodName, UUID userId, String productType) {
        return methodName + ":" + userId + ":" + productType;
    }

    public void clearCaches() {
        cache.invalidateAll();
    }
}