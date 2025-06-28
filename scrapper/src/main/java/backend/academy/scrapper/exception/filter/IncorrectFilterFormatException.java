package backend.academy.scrapper.exception.filter;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;

public class IncorrectFilterFormatException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = -1593128914346185461L;

    public IncorrectFilterFormatException() {
        super(
                ExceptionMessageValues.INCORRECT_FILTER_FORMAT_EXCEPTION_MESSAGE,
                ExceptionDescriptionValues.INCORRECT_FILTER_FORMAT_EXCEPTION_DESCRIPTION);
    }
}
