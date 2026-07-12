package edu.mscs535.securedirectory.config;

import edu.mscs535.securedirectory.security.DatabaseUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            @Value("${app.http-port:8080}") int httpPort,
                                            @Value("${server.port:8443}") int httpsPort) throws Exception {
        PortMapperImpl portMapper = new PortMapperImpl();
        portMapper.setPortMappings(Map.of(Integer.toString(httpPort), Integer.toString(httpsPort)));

        http
                .portMapper(mapper -> mapper.portMapper(portMapper))
                .redirectToHttps(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/error", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/employees", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(DatabaseUserDetailsService userDetailsService,
                                                     PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
