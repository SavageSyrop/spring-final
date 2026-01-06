package own.savage.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(@Autowired JwtTokenService jwtService, @Autowired ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Извлекаем JWT из заголовка
        String token = extractJwtFromRequest(request);

        if (token != null && jwtService.validateToken(token)) {
            // Парсим claims из JWT
            Claims claims = jwtService.parseClaims(token);

            // Создаем внутренние данные авторизации
            InternalAuthData authContext = InternalAuthData.builder()
                    .username(claims.getSubject())
                    .roles(claims.get("roles", List.class))
                    .build();

            // Кодируем в Base64 для передачи
            String encodedContext = Base64.getEncoder()
                    .encodeToString(objectMapper.writeValueAsBytes(authContext));

            // Создаем обертку запроса с новыми заголовками
            ServletRequest wrappedRequest;

            wrappedRequest = new ServletRequestWrapper(request);

            wrappedRequest.setAttribute("X-Internal-Auth", encodedContext);
            wrappedRequest.removeAttribute("Authorization");

            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
