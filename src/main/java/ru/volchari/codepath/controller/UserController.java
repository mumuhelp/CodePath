package ru.volchari.codepath.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volchari.codepath.dto.UserDto;
import ru.volchari.codepath.model.User;
import ru.volchari.codepath.repository.UserRepository;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Возвращаем DTO с данными пользователя
        return ResponseEntity.ok(UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .avatar(user.getAvatar())
                .build());
    }
}