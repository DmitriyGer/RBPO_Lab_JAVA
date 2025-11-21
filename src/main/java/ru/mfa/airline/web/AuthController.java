package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            validatePassword(request.getPassword());

            String role = request.getRole();
            if (role == null || role.isEmpty()) {
                role = "ROLE_USER";
            }
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

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
    }
}
