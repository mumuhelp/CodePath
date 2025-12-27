package ru.volchari.codepath.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.volchari.codepath.service.JwtService;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromCookie(request);

        // Если токен есть и он валиден
        if (token != null && jwtService.validateToken(token)) {
            String email = jwtService.extractEmail(token);

            // Создаем объект аутентификации для Spring Security
            // В третьем параметре можно передать список ролей (Authorities)
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    new ArrayList<>() // Здесь будут роли, если ты их достанешь из токена
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Ключевой момент: кладем юзера в "контекст", теперь Spring знает, кто делает запрос
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Идем дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}