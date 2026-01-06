package own.savage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public InternalAuthFilter(@Autowired ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String internalAuthHeader = request.getHeader("X-Internal-Auth");

        if (StringUtils.hasText(internalAuthHeader)) {
            try {

                byte[] decodedBytes = Base64.getDecoder().decode(internalAuthHeader);
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
                logger.warn("Failed to parse internal auth header", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}

