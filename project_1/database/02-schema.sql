-- Run in the secure_directory database as its owner, not as the application role.
CREATE TABLE app_user (
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(60) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE employee (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(80) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON app_user, employee FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO secure_directory_app;
GRANT SELECT ON app_user, employee TO secure_directory_app;

-- The application receives read-only table access and no DDL or role-management rights.
