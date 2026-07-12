# Secure Employee Directory

Project 1 for **MSCS-535, Secure Software Development**. This small Spring Boot application demonstrates secure access to PostgreSQL through an HTTPS web application.

## What the Project Demonstrates

- Parameterized JDBC queries that prevent SQL injection
- HTTPS on port 8443 with HTTP-to-HTTPS redirection
- Database-backed login with BCrypt password hashes
- Database and certificate secrets supplied through environment variables
- A read-only, least-privilege PostgreSQL application role
- A phishing reminder that passwords should never be sent through email

Spring Security also supplies standard session, CSRF, and security-header protections without custom authentication infrastructure.

## Local Setup and Run

Install a JDK (version 17 or newer), Maven, PostgreSQL, and OpenSSL. On macOS with Homebrew:

```bash
brew install openjdk maven postgresql
brew services start postgresql
```

Then run the setup script once. It creates a Git-ignored `.env` file with random local secrets, configures PostgreSQL, asks you to choose the `student` password, and creates a self-signed development certificate.

```bash
./scripts/setup-local.sh
```

Do not start the application until the script prints `Setup complete.` It verifies that the `student` account was created before doing so.

Start the application:

```bash
mvn spring-boot:run
```

Run the automated tests at any time:

```bash
mvn test
```

Spring Boot automatically reads `.env` when the application starts. Do not commit that file. A browser will warn about the self-signed certificate until it is trusted locally; this is expected for local development.

If PostgreSQL is on another machine, set `DB_URL` in `.env` to use PostgreSQL certificate verification rather than an unverified encrypted connection:

```bash
DB_URL=jdbc:postgresql://db.example.com:5432/secure_directory?sslmode=verify-full&sslrootcert=/path/to/ca.crt
```

Open `https://localhost:8443/login` and sign in as `student` with the password selected during setup. Requests to `http://localhost:8080` redirect to HTTPS.

## Why the SQL Is Safe

Repository queries use JDBC placeholders instead of joining user input into SQL:

```java
String sql = "SELECT username, password_hash, enabled FROM app_user WHERE username = ?";
jdbcTemplate.query(sql, rowMapper, username);
```

The SQL command and the submitted value are sent separately, so characters such as quotes remain data and cannot change the query. Search validation improves input quality, but parameter binding is the protection against SQL injection. See `docs/sql-injection-example.md` for a short insecure-versus-secure comparison.

HTTPS protects credentials and results between the browser and application. It does not prove that a link in an email is legitimate, so the login page also reminds employees that IT will never request passwords by email. In a production system, phishing resistance should additionally include MFA, email filtering, training, and independent verification procedures.
