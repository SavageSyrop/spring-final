package own.savage.security;

import io.micrometer.core.instrument.util.IOUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class ReadWriteCorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String correlationIdValue = request.getHeader("X-Correlation-Id");
        if (correlationIdValue == null || correlationIdValue.isEmpty()) {
            correlationIdValue = UUID.randomUUID().toString();
        }
        response.setHeader("X-Correlation-Id", correlationIdValue);
        request.setAttribute("correlationId", correlationIdValue);
        log.debug("[{}] {} {} {}", correlationIdValue, request.getMethod(), request.getRequestURI(), IOUtils.toString(request.getInputStream()));
        filterChain.doFilter(request, response);
    }
}


