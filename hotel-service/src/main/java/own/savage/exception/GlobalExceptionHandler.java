package own.savage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ExceptionBody>> handleIllegalStateExceptionException(ServerWebExchange exchange, IllegalStateException ex) {

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionBody(ex.getMessage(), exchange.getRequest().getHeaders().get("X-Correlation-Id"))));
    }

    @ExceptionHandler(value = {AuthenticationException.class, AuthorizationServiceException.class})
    public Mono<ResponseEntity<ExceptionBody>> handleAuthenticationException(ServerWebExchange exchange, Exception ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ExceptionBody(ex.getMessage(), exchange.getRequest().getHeaders().get("X-Correlation-Id"))));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ExceptionBody>> handleException(ServerWebExchange exchange, Exception ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionBody(ex.getMessage(), exchange.getRequest().getHeaders().get("X-Correlation-Id"))));
    }
}
