-- Run as a PostgreSQL administrator. Supply app_db_password with psql --set.
CREATE ROLE secure_directory_app
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    PASSWORD :'app_db_password';

GRANT CONNECT ON DATABASE secure_directory TO secure_directory_app;
