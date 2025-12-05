package ru.mfa.airline.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mfa.airline.dto.LoginRequest;
import ru.mfa.airline.dto.RefreshRequest;
import ru.mfa.airline.dto.RegisterRequest;
import ru.mfa.airline.dto.TokenPairResponse;
import ru.mfa.airline.model.User;
import ru.mfa.airline.service.AuthService;
import ru.mfa.airline.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TokenPairResponse tokenPair = authService.login(request);
            return ResponseEntity.ok(tokenPair);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            TokenPairResponse tokenPair = authService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(tokenPair);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
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
