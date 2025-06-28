package backend.academy.scrapper.exception.link;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;
import java.net.URI;
import java.util.List;

public class LinkNotSupportedException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 610277758084875143L;

    public LinkNotSupportedException(URI url, List<String> availableUrls) {
        super(
                ExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE,
                String.format(
                        ExceptionDescriptionValues.LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION
                                + "%n"
                                + ExceptionDescriptionValues.AVAILABLE_LINKS_DESCRIPTION,
                        url,
                        String.join("\n", availableUrls)));
    }

    public LinkNotSupportedException(URI url) {
        super(
                ExceptionMessageValues.LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE,
                String.format(ExceptionDescriptionValues.LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION, url));
    }
}
