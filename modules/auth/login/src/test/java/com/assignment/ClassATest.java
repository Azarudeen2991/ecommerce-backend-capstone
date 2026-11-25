package com.assignment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.assignment.dtos.LoginRequestDto;
import com.assignment.exceptions.PasswordMismatchException;
import com.assignment.exceptions.UserNotFoundException;
import com.assignment.models.User;
import com.assignment.repos.UserRepository;
import com.assignment.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClassATest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void seedUser(String email, String rawPassword) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(email);
        u.setPassword(bCryptPasswordEncoder.encode(rawPassword));
        userRepository.save(u);
    }

    // ---------------- Controller tests ----------------

    @Test
    void loginSuccess_shouldReturnSUCCESS_andTokenAlphanumericLength15() throws Exception {
        seedUser("test@example.com", "pass123");

        String json = """
            {
              "email": "test@example.com",
              "password": "pass123"
            }
            """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").value(matchesPattern("^[A-Za-z0-9]{15}$")));
    }

    @Test
    void loginUserNotFound_shouldReturnFAILURE_andNullToken() throws Exception {
        String json = """
            {
              "email": "nouser@example.com",
              "password": "pass123"
            }
            """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.token", nullValue()));
    }

    @Test
    void loginWrongPassword_shouldReturnFAILURE_andNullToken() throws Exception {
        seedUser("test@example.com", "pass123");

        String json = """
            {
              "email": "test@example.com",
              "password": "wrongpass"
            }
            """;

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILURE"))
                .andExpect(jsonPath("$.token", nullValue()));
    }

    @Test
    void loginSuccess_twoCalls_shouldReturnDifferentTokens() throws Exception {
        seedUser("same@example.com", "pass123");

        String json = """
            {
              "email": "same@example.com",
              "password": "pass123"
            }
            """;

        MvcResult r1 = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andReturn();

        MvcResult r2 = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andReturn();

        JsonNode n1 = objectMapper.readTree(r1.getResponse().getContentAsString());
        JsonNode n2 = objectMapper.readTree(r2.getResponse().getContentAsString());

        String t1 = n1.get("token").asText();
        String t2 = n2.get("token").asText();

        assertNotNull(t1);
        assertNotNull(t2);
        assertEquals(15, t1.length());
        assertEquals(15, t2.length());
        assertTrue(t1.matches("^[A-Za-z0-9]{15}$"));
        assertTrue(t2.matches("^[A-Za-z0-9]{15}$"));

        // extremely low collision probability
        assertNotEquals(t1, t2);
    }

    // ---------------- Service tests (exact exception messages) ----------------

    @Test
    void service_userNotFound_shouldThrowWithExactMessage() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("nouser@example.com");
        dto.setPassword("pass123");

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userService.login(dto)
        );

        assertEquals(
                "There is no account with an email address: nouser@example.com",
                ex.getMessage()
        );
    }

    @Test
    void service_passwordMismatch_shouldThrowWithExactMessage() {
        seedUser("test@example.com", "pass123");

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("test@example.com");
        dto.setPassword("wrongpass");

        PasswordMismatchException ex = assertThrows(
                PasswordMismatchException.class,
                () -> userService.login(dto)
        );

        assertEquals(
                "Please type correct password, or reset it",
                ex.getMessage()
        );
    }

    @Test
    void service_success_shouldReturnTokenLength15_alphanumeric() {
        seedUser("ok@example.com", "pass123");

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("ok@example.com");
        dto.setPassword("pass123");

        String token = userService.login(dto);

        assertNotNull(token);
        assertEquals(15, token.length());
        assertTrue(token.matches("^[A-Za-z0-9]{15}$"));
    }
}
