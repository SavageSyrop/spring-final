package own.savage.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerAdviceExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody handleResourceNotFound(WebRequest request, EntityNotFoundException e) {
        return new ExceptionBody(e.getMessage(), request.getHeader("X-Correlation-Id"));
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(WebRequest request, IllegalStateException e) {
        return new ExceptionBody(e.getMessage(), request.getHeader("X-Correlation-Id"));
    }

    @ExceptionHandler({AccessDeniedException.class, ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAccessDenied(WebRequest request, RuntimeException e) {
        return new ExceptionBody(e.getMessage(), request.getHeader("X-Correlation-Id"));
    }

    @ExceptionHandler(value = {AuthenticationException.class, AuthorizationServiceException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionBody handleAuthentication(WebRequest request, RuntimeException e) {
        return new ExceptionBody(e.getMessage(), request.getHeader("X-Correlation-Id"));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleException(WebRequest request, Exception e) {
        e.printStackTrace();
        return new ExceptionBody("Internal error", request.getHeader("X-Correlation-Id"));
    }
}
