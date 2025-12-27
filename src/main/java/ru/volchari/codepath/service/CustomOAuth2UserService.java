package ru.volchari.codepath.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ru.volchari.codepath.model.Provider;
import ru.volchari.codepath.model.User;
import ru.volchari.codepath.repository.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final UserRepository userRepository;
    private static final String GITHUB_EMAILS_API = "https://api.github.com/user/emails";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Получаем базовый объект пользователя (login, id и т.д. - email: null)
        OAuth2User oauth2User = super.loadUser(userRequest);

        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2. Обрабатываем только GitHub
        if (Provider.GITHUB.getName().equals(clientRegistrationId)) {

            // 3. Получаем Access Token
            String accessToken = userRequest.getAccessToken().getTokenValue();

            try {
                // 4. Формируем запрос к конечной точке /user/emails (без RestTemplate)
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GITHUB_EMAILS_API))
                        .header("Authorization", "token " + accessToken) // GitHub принимает 'token'
                        .header("Accept", "application/vnd.github.v3+json")
                        .GET()
                        .build();

                // Отправляем синхронный запрос
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 5. Парсим ответ (массив EmailDto)
                if (response.statusCode() == 200) {

                    // GitHub возвращает JSON-массив, парсим его
                    List<Map<String, Object>> emails = objectMapper.readValue(
                            response.body(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
                    );

                    // 6. Ищем первичный и подтвержденный email
                    String primaryEmail = emails.stream()
                            .filter(e -> (Boolean) e.get("primary"))
                            .filter(e -> (Boolean) e.get("verified"))
                            .map(e -> (String) e.get("email"))
                            .findFirst()
                            .orElse(null);

                    // 7. Обновляем атрибуты пользователя
                    if (primaryEmail != null) {
                        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
                        attributes.put("email", primaryEmail); // Перезаписываем 'email: null'

                        // Сохраняем/обновляем пользователя в БД
                        String name = attributes.get("name") != null
                                ? (String) attributes.get("name")
                                : (String) attributes.get("login");
                        String avatar = (String) attributes.get("avatar_url");
                        
                        User user = userRepository.findByEmail(primaryEmail)
                                .map(existingUser -> updateExistingUser(existingUser, name, avatar, Provider.GITHUB))
                                .orElseGet(() -> registerNewUser(primaryEmail, name, avatar, Provider.GITHUB));

                        // Возвращаем нового пользователя с обновленными атрибутами
                        return new DefaultOAuth2User(
                                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                                attributes,
                                "email"
                        );
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Fallback для других провайдеров или если GitHub email получен напрямую
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        String name = oauth2User.getAttribute("name");
        String avatar = oauth2User.getAttribute("avatar_url") != null 
                ? oauth2User.getAttribute("avatar_url") 
                : oauth2User.getAttribute("picture");
        Provider provider = Provider.valueOf(clientRegistrationId.toUpperCase());


        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, avatar, provider))
                .orElseGet(() -> registerNewUser(email, name, avatar, provider));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                oauth2User.getAttributes(),
                "email"
        );
    }

    private User registerNewUser(String email, String name, String avatar, Provider provider) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setAvatar(avatar);
        user.setProvider(provider);
        return userRepository.save(user);
    }

    private void checkProvider(User user, Provider provider) {
        if (!user.getProvider().equals(provider)) {
            String existingProvider = switch (user.getProvider()) {
                case GITHUB -> "GitHub";
                case GOOGLE -> "Google";
                case LOCAL -> "email и пароль";
            };
            String message = "Этот аккаунт зарегистрирован через " + existingProvider + ". Используйте вход через " + existingProvider + ".";
            OAuth2Error error = new OAuth2Error("provider_mismatch", message, null);
            throw new OAuth2AuthenticationException(error, message);
        }
    }

    private User updateExistingUser(User existingUser, String name, String avatar, Provider provider) {
        checkProvider(existingUser, provider);
        boolean modified = false;

        if (name != null && !name.equals(existingUser.getName())) {
            existingUser.setName(name);
            modified = true;
        }

        if (avatar != null && !avatar.equals(existingUser.getAvatar())) {
            existingUser.setAvatar(avatar);
            modified = true;
        }

        if (modified) {
            return userRepository.save(existingUser);
        }

        return existingUser;
    }
}