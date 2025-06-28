package backend.academy.scrapper.exception.link;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;
import java.net.URI;

public class LinkHasAlreadyAddedException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = -7963495956191035950L;

    public LinkHasAlreadyAddedException(URI url, Long chatId) {
        super(
                ExceptionMessageValues.LINK_HAS_ALREADY_ADDED_EXCEPTION_MESSAGE,
                String.format(ExceptionDescriptionValues.LINK_HAS_ALREADY_ADDED_EXCEPTION_DESCRIPTION, url, chatId));
    }
}
