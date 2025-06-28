package backend.academy.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class NotFoundException extends LinkTrackerException {
    @Serial
    private static final long serialVersionUID = -2540293337365788726L;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public NotFoundException(String message, String description) {
        super(message, description);
    }
}
