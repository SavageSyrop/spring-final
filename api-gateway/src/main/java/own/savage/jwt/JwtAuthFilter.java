package own.savage.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtTokenService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(@Autowired JwtTokenService jwtService, @Autowired ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractJwtFromRequest(exchange);

        if (token != null && jwtService.validateToken(token)) {
            // Парсим claims из JWT
            Claims claims = jwtService.parseClaims(token);

            // Создаем внутренние данные авторизации
            InternalAuthData authContext = InternalAuthData.builder()
                    .username(claims.getSubject())
                    .roles(claims.get("roles", List.class))
                    .build();

            // Кодируем в Base64 для передачи
            String encodedContext;
            try {
                encodedContext = Base64.getEncoder()
                        .encodeToString(objectMapper.writeValueAsBytes(authContext));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return chain.filter(
                    exchange.mutate().request(
                                    exchange.getRequest().mutate()
                                            .header("X-Internal-Auth", encodedContext)
                                            .build())
                            .build());
        } else {
            return chain.filter(exchange);
        }
    }

    private String extractJwtFromRequest(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> authValues = headers.get("Authorization");

        if (authValues == null || authValues.isEmpty()) {
            return null;
        } else {
            return authValues.get(0).substring(7);
        }
    }
}


