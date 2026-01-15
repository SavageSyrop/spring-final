package own.savage.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExceptionBody {

    private String message;
    private String correlationId;

    public ExceptionBody(String message, List<String> correlationIdList) {
        this.message = message;
        if (correlationIdList.isEmpty()) {
            this.correlationId = null;
        } else {
            this.correlationId = correlationIdList.get(0);
        }

    }
}
