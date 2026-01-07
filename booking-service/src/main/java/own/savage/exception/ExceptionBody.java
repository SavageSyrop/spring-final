package own.savage.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionBody {

    private String message;
    private String correlationId;

    public ExceptionBody(String message, String correlationId) {
        this.message = message;
        this.correlationId = correlationId;
    }
}
