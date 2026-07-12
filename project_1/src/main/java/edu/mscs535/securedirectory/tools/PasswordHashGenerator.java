package edu.mscs535.securedirectory.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Local setup helper. Reads one password from standard input and prints its BCrypt hash.
 */
public final class PasswordHashGenerator {

    private PasswordHashGenerator() {
    }

    public static void main(String[] args) throws Exception {
        String password = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("A non-empty password is required.");
        }
        System.out.println(new BCryptPasswordEncoder(12).encode(password));
    }
}
