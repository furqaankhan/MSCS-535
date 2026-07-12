# SQL Injection Example

## Insecure Example, Never Used by the Application

The following code is intentionally vulnerable because it treats untrusted input as part of the SQL command. It is documentation only and is not compiled or called.

```java
String sql = "SELECT * FROM app_user WHERE username = '" + username + "'";
```

An attacker can enter a value containing SQL syntax so the database interprets that value as executable command text.

## Secure Replacement Used by the Application

`UserAccountRepository.findByUsername` uses a placeholder and passes the value separately:

```java
String sql = """
        SELECT username, password_hash, enabled
        FROM app_user
        WHERE username = ?
        """;

jdbcTemplate.query(sql, rowMapper, username);
```

The JDBC driver sends the SQL structure separately from the bound username. The username therefore remains data even if it contains quote characters or SQL keywords. `EmployeeRepository.search` uses the same binding mechanism for all search values.
