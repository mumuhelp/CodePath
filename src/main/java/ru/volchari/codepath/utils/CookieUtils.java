package ru.volchari.codepath.utils;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public HttpCookie createAccessTokenCookie(String token, long durationMillis) {
        return ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true) // В продакшене (HTTPS) ставь true
                .path("/")
                .maxAge(durationMillis / 1000)
                .sameSite("Lax") // Защита от CSRF
                .build();
    }

    public HttpCookie createRefreshTokenCookie(String token, long durationMillis) {
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(durationMillis / 1000)
                .sameSite("Lax")
                .build();
    }

    public HttpCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true) // true в проде
                .path("/")
                .maxAge(0) // Удаляет мгновенно
                .build();
    }
}