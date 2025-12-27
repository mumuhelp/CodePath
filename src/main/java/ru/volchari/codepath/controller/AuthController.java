package ru.volchari.codepath.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volchari.codepath.dto.LoginRequest;
import ru.volchari.codepath.dto.RegistrationRequest;
import ru.volchari.codepath.dto.UserDto;
import ru.volchari.codepath.repository.UserRepository;
import ru.volchari.codepath.utils.CookieUtils;
import ru.volchari.codepath.model.User;
import ru.volchari.codepath.service.AuthService;
import ru.volchari.codepath.service.JwtService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
        User user = authService.registerUser(registrationRequest);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createAccessTokenCookie(accessToken, 900000).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createRefreshTokenCookie(refreshToken, 2592000000L).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        User user = authService.authenticateUser(loginRequest);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createAccessTokenCookie(accessToken, 900000).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createRefreshTokenCookie(refreshToken, 2592000000L).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token") String refreshToken,
                                     HttpServletResponse response) {

        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid Refresh Token");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow();

        String newAccessToken = jwtService.generateAccessToken(user);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createAccessTokenCookie(newAccessToken, 900000).toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteCookie("access_token").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteCookie("refresh_token").toString());
        return ResponseEntity.ok().build();
    }
}
