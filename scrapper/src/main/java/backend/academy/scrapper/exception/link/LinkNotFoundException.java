package backend.academy.scrapper.exception.link;

import backend.academy.common.exception.NotFoundException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;
import java.net.URI;

public class LinkNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 2316298341098038019L;

    public LinkNotFoundException(URI url, Long chatId) {
        super(
                ExceptionMessageValues.LINK_NOT_FOUND_EXCEPTION_MESSAGE,
                String.format(ExceptionDescriptionValues.LINK_NOT_FOUND_EXCEPTION_DESCRIPTION, url, chatId));
    }
}
