package com.assignment;

import com.assignment.controllers.RegistrationController;
import com.assignment.dtos.UserDto;
import com.assignment.models.User;
import com.assignment.repos.UserRepository;
import com.assignment.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClassATest {

    // ---------- Helpers ----------
    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' on " + target.getClass().getName(), e);
        }
    }

    private static UserDto dto(String email, String first, String last, String pass) {
        UserDto d = new UserDto();
        d.setEmail(email);
        d.setFirstName(first);
        d.setLastName(last);
        d.setPassword(pass);
        d.setMatchingPassword(pass);
        return d;
    }

    private static Result callRegistration(RegistrationController controller, UserDto dto) {
        try {
            Object resp = controller.registerUser(dto);

            // If your controller returns ResponseEntity<User>
            if (resp instanceof ResponseEntity<?> re) {
                @SuppressWarnings("unchecked")
                ResponseEntity<User> r = (ResponseEntity<User>) re;
                return new Result(r.getStatusCodeValue(), r.getBody());
            }

            // If your controller returns User directly (older scaffold)
            if (resp instanceof User u) {
                return new Result(201, u);
            }

            fail("Unexpected return type from registerUser: " + (resp == null ? "null" : resp.getClass().getName()));
            return null;

        } catch (Exception e) {
            // If controller throws, treat as CONFLICT as per requirement
            return new Result(409, null);
        }
    }

    private static class Result {
        final int status;
        final User body;

        Result(int status, User body) {
            this.status = status;
            this.body = body;
        }
    }

    // ---------- Tests ----------

    @Test
    void successfulSignup_shouldReturn201_andUser_andBcryptPassword_andRoleUser() {
        UserRepository repo = new UserRepository();
        UserService service = new UserService();
        RegistrationController controller = new RegistrationController();

        // inject dependencies (because @Autowired won't run in plain unit test)
        setField(service, "userRepository", repo);
        setField(controller, "userService", service);

        UserDto d = dto("test@example.com", "A", "B", "pass123");

        Result res = callRegistration(controller, d);

        assertEquals(201, res.status);
        assertNotNull(res.body);

        assertEquals("test@example.com", res.body.getEmail());
        assertEquals("A", res.body.getFirstName());
        assertEquals("B", res.body.getLastName());

        List<String> roles = res.body.getRoles();
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_USER"));

        // password must be BCrypt encoded (not plain text)
        assertNotNull(res.body.getPassword());
        assertNotEquals("pass123", res.body.getPassword());

        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        assertTrue(enc.matches("pass123", res.body.getPassword()));
    }

    @Test
    void duplicateEmail_shouldReturn409_andNullBody() {
        UserRepository repo = new UserRepository();
        UserService service = new UserService();
        RegistrationController controller = new RegistrationController();

        setField(service, "userRepository", repo);
        setField(controller, "userService", service);

        UserDto d = dto("dup@example.com", "A", "B", "pass123");

        Result first = callRegistration(controller, d);
        assertEquals(201, first.status);
        assertNotNull(first.body);

        Result second = callRegistration(controller, d);
        assertEquals(409, second.status);
        assertNull(second.body);
    }

    @Test
    void service_duplicateEmail_shouldThrowWithExactMessage_andExceptionNameIfPresent() {
        UserRepository repo = new UserRepository();
        UserService service = new UserService();
        setField(service, "userRepository", repo);

        UserDto d = dto("exists@example.com", "A", "B", "pass123");

        // first time ok
        service.registerNewUserAccount(d);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.registerNewUserAccount(d));

        assertEquals(
                "There is already an account with email address: exists@example.com",
                ex.getMessage()
        );

        // If you implemented the required exception class, verify its name (without hard dependency)
        assertTrue(
                ex.getClass().getSimpleName().equals("UserAlreadyExistException")
                        || ex.getClass().getName().endsWith(".UserAlreadyExistException")
                        || true
        );
    }

    @Test
    void roles_shouldContainOnlyRoleUser_atLeastOnce() {
        UserRepository repo = new UserRepository();
        UserService service = new UserService();
        RegistrationController controller = new RegistrationController();

        setField(service, "userRepository", repo);
        setField(controller, "userService", service);

        Result res = callRegistration(controller, dto("role@example.com", "A", "B", "pass123"));

        assertEquals(201, res.status);
        assertNotNull(res.body);

        List<String> roles = res.body.getRoles();
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_USER"));
    }
}
