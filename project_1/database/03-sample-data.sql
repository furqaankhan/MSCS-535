-- Supply bootstrap_password_hash with psql --set. It must be a BCrypt hash.
INSERT INTO app_user (username, password_hash)
VALUES ('student', :'bootstrap_password_hash');

INSERT INTO employee (employee_number, full_name, department, email) VALUES
    ('E-1001', 'Avery Morgan', 'Engineering', 'avery.morgan@example.test'),
    ('E-1002', 'Jordan Lee', 'Finance', 'jordan.lee@example.test'),
    ('E-1003', 'Samira Patel', 'Information Security', 'samira.patel@example.test'),
    ('E-1004', 'Mateo Rivera', 'Human Resources', 'mateo.rivera@example.test');
