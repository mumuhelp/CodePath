package ru.volchari.codepath.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.volchari.codepath.dto.LoginRequest;
import ru.volchari.codepath.dto.RegistrationRequest;
import ru.volchari.codepath.model.Provider;
import ru.volchari.codepath.model.User;
import ru.volchari.codepath.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User registerUser(RegistrationRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Пользователь с почтой %s уже существует"
                    .formatted(registerRequest.getEmail()));
        }

        if (userRepository.existsByName(registerRequest.getName())) {
            throw new RuntimeException("Пользователь с именем %s уже существует"
                    .formatted(registerRequest.getName()));
        }

        User user = new User();
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setProvider(Provider.LOCAL);

        return userRepository.save(user);
    }

    public User authenticateUser(LoginRequest loginRequest) {
        // Проверяем, существует ли пользователь и через какой провайдер он зарегистрирован
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Неверный email"));

        if (user.getProvider() != Provider.LOCAL) {
            String providerName = switch (user.getProvider()) {
                case GITHUB -> "GitHub";
                case GOOGLE -> "Google";
                default -> user.getProvider().name();
            };
            throw new RuntimeException("Этот аккаунт зарегистрирован через %s. Используйте вход через %s."
                    .formatted(providerName, providerName));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            return user;

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Неверный email или пароль");
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(email));
    }
}