package backend.academy.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BadRequestException extends LinkTrackerException {
    @Serial
    private static final long serialVersionUID = -1973239065881469425L;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    public BadRequestException(String message, String description) {
        super(message, description);
    }
}
