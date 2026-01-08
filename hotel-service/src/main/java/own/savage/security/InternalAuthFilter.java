package own.savage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InternalAuthFilter implements WebFilter {

    private final ObjectMapper objectMapper;

    public InternalAuthFilter(@Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> internalAuthHeaders = headers.get("X-Internal-Auth");

        String authValue;
        if (internalAuthHeaders == null || internalAuthHeaders.isEmpty()) {
            return chain.filter(exchange);
        } else {
            authValue = internalAuthHeaders.get(0);
        }


        if (StringUtils.hasText(authValue)) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(authValue);
                InternalAuthData authContext = objectMapper.readValue(decodedBytes, InternalAuthData.class);

                List<GrantedAuthority> authorities = authContext.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

                InternalAuthenticationToken authToken = new InternalAuthenticationToken(
                        authContext.getUsername(),
                        authorities,
                        authContext
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                log.warn("Failed to parse internal auth header", e);
            }
        }
        return chain.filter(exchange);
    }
}

