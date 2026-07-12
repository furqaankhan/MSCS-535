package edu.mscs535.securedirectory.employee;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Employee> search(String searchTerm) {
        String sql = """
                SELECT id, employee_number, full_name, department, email
                FROM employee
                WHERE LOWER(full_name) LIKE LOWER(?)
                   OR LOWER(employee_number) = LOWER(?)
                   OR LOWER(department) LIKE LOWER(?)
                ORDER BY full_name
                LIMIT 50
                """;
        String partialMatch = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, (resultSet, rowNumber) -> new Employee(
                resultSet.getLong("id"),
                resultSet.getString("employee_number"),
                resultSet.getString("full_name"),
                resultSet.getString("department"),
                resultSet.getString("email")), partialMatch, searchTerm, partialMatch);
    }
}
