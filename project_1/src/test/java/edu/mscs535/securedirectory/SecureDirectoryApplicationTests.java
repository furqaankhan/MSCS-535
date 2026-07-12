package edu.mscs535.securedirectory;

import edu.mscs535.securedirectory.employee.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SecureDirectoryApplicationTests {

    private static final String TEST_PASSWORD = "Correct-Horse-47";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void resetAccount() {
        jdbcTemplate.update("DELETE FROM app_user WHERE username = ?", "student");
        String hash = new BCryptPasswordEncoder(4).encode(TEST_PASSWORD);
        jdbcTemplate.update("INSERT INTO app_user (username, password_hash) VALUES (?, ?)", "student", hash);
    }

    @Test
    void successfulLoginAuthenticatesUser() throws Exception {
        mockMvc.perform(loginRequest(TEST_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(authenticated().withUsername("student"));
    }

    @Test
    void failedLoginUsesGenericResponse() throws Exception {
        mockMvc.perform(loginRequest("incorrect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        mockMvc.perform(get("/login?error").with(httpsRequest()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Sign-in failed. Check your credentials or try again later.")));
    }

    @Test
    void validEmployeeSearchReturnsMatchingRecord() throws Exception {
        mockMvc.perform(get("/employees").param("query", "Avery").with(httpsRequest()).with(user("student")))
                .andExpect(status().isOk())
                .andExpect(view().name("employees"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Avery Morgan")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Jordan Lee"))));
    }

    @Test
    void sqlInjectionTextIsDataAndDoesNotReturnAllRows() {
        assertThat(employeeRepository.search("' OR '1'='1")).isEmpty();
    }

    @Test
    void invalidSearchInputIsRejectedBeforeDatabaseSearch() throws Exception {
        mockMvc.perform(get("/employees").param("query", "' OR 1=1; --").with(httpsRequest()).with(user("student")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Use letters, numbers")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Avery Morgan"))));
    }

    @Test
    void unauthorizedRequestRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/employees").with(httpsRequest()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://localhost:8443/login"));
    }

    @Test
    void httpRequestRedirectsToConfiguredHttpsPort() throws Exception {
        mockMvc.perform(get("/employees").secure(false).with(request -> {
                    request.setServerPort(8080);
                    return request;
                }))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://localhost:8443/employees"));
    }

    @Test
    void csrfProtectionRejectsLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/logout").with(httpsRequest()).with(user("student")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/logout").with(httpsRequest()).with(user("student")).with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder loginRequest(String password) {
        return post("/login")
                .param("username", "student")
                .param("password", password)
                .with(httpsRequest())
                .with(csrf());
    }

    private RequestPostProcessor httpsRequest() {
        return request -> {
            request.setScheme("https");
            request.setSecure(true);
            request.setServerPort(8443);
            return request;
        };
    }
}
