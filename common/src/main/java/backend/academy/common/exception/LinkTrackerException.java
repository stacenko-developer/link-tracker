package backend.academy.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class LinkTrackerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4502630946417798520L;

    protected final String description;

    public abstract HttpStatus getHttpStatus();

    public LinkTrackerException(String message, String description) {
        super(message);
        this.description = description;
    }
}
