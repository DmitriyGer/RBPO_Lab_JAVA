package ru.mfa.airline.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.dto.RegisterRequest;
import ru.mfa.airline.model.User;
import ru.mfa.airline.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            validateUsername(request.getUsername());
            validatePassword(request.getPassword());

            String role = normalizeRole(request.getRole());

            User user = userService.createUser(request.getUsername(), request.getPassword(), role);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/csrf")
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        if (!hasSpecialChar) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }

        boolean hasDigit = password.matches(".*\\d.*");
        if (!hasDigit) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        if (!hasUpperCase) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        boolean hasLowerCase = password.matches(".*[a-z].*");
        if (!hasLowerCase) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }

        if (!username.matches("^[A-Za-z0-9_.-]{3,50}$")) {
            throw new IllegalArgumentException(
                    "Username must be 3-50 characters and contain only letters, numbers, dot, dash or underscore");
        }
    }

    private String normalizeRole(String incomingRole) {
        String role = (incomingRole == null || incomingRole.isBlank()) ? "USER" : incomingRole.trim();
        role = role.toUpperCase();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return role;
    }
}
