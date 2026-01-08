package own.savage.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ReadWriteCorrelationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        List<String> correlationIdValues = headers.get("X-Correlation-Id");
        String correlationValue;
        if (correlationIdValues == null || correlationIdValues.isEmpty()) {
            correlationValue = UUID.randomUUID().toString();
        } else {
            correlationValue = correlationIdValues.get(0);
        }
        log.debug("[{}] {} {}", correlationValue, request.getMethod(), exchange.getRequest().getPath());

        return chain.filter(
                exchange.mutate().request(
                                exchange.getRequest().mutate()
                                        .header("X-Correlation-Id", correlationValue)
                                        .build())
                        .build());
    }
}


