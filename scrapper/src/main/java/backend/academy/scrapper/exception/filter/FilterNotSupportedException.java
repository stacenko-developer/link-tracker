package backend.academy.scrapper.exception.filter;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;
import java.net.URI;
import java.util.List;

public class FilterNotSupportedException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = -7963495956191035950L;

    public FilterNotSupportedException(String filter, URI url, List<String> availableFilters) {
        super(
                ExceptionMessageValues.FILTER_NOT_SUPPORTED_EXCEPTION_MESSAGE,
                String.format(
                        ExceptionDescriptionValues.FILTER_NOT_SUPPORTED_EXCEPTION_DESCRIPTION,
                        filter,
                        url,
                        availableFilters));
    }
}
