package ru.volchari.codepath.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import ru.volchari.codepath.utils.CookieUtils;
import ru.volchari.codepath.model.User;
import ru.volchari.codepath.repository.UserRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CookieUtils cookieUtils;
    
    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow();

        // Генерируем токены
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Кладем в куки
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createAccessTokenCookie(accessToken, 900000).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtils.createRefreshTokenCookie(refreshToken, 2592000000L).toString());

        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard");
    }
}