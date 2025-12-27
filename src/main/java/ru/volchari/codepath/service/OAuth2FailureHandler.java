package ru.volchari.codepath.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorMessage = extractErrorMessage(exception);
        
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response,
                frontendUrl + "/login?error=" + encodedMessage);
    }

    private String extractErrorMessage(AuthenticationException exception) {
        // Для OAuth2AuthenticationException извлекаем description из OAuth2Error
        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            String description = oauth2Exception.getError().getDescription();
            if (description != null && !description.isEmpty()) {
                return description;
            }
        }
        
        // Fallback на message
        return exception.getMessage() != null ? exception.getMessage() : "Ошибка авторизации";
    }
}
