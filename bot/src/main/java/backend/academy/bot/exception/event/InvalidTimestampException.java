package backend.academy.bot.exception.event;

import backend.academy.bot.constants.exception.ExceptionDescriptionValues;
import backend.academy.bot.constants.exception.ExceptionMessageValues;
import backend.academy.common.exception.BadRequestException;
import java.io.Serial;

public class InvalidTimestampException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = -3257419681021684398L;

    public InvalidTimestampException() {
        super(
                ExceptionMessageValues.INVALID_TIMESTAMP_EXCEPTION_MESSAGE,
                ExceptionDescriptionValues.INVALID_TIMESTAMP_EXCEPTION_DESCRIPTION);
    }
}
