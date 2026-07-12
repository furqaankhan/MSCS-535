package edu.mscs535.securedirectory.account;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserAccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserAccount> findByUsername(String username) {
        String sql = """
                SELECT username, password_hash, enabled
                FROM app_user
                WHERE username = ?
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new UserAccount(
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getBoolean("enabled")), username).stream().findFirst();
    }
}
