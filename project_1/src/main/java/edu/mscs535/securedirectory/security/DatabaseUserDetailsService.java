package edu.mscs535.securedirectory.security;

import edu.mscs535.securedirectory.account.UserAccount;
import edu.mscs535.securedirectory.account.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repository;

    public DatabaseUserDetailsService(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return User.withUsername(account.username())
                .password(account.passwordHash())
                .roles("USER")
                .disabled(!account.enabled())
                .build();
    }
}
