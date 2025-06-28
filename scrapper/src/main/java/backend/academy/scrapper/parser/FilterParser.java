package backend.academy.scrapper.parser;

import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.exception.filter.IncorrectFilterFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class FilterParser {

    private static final Pattern LOG_PATTERN = Pattern.compile("(\\S+):(\\S+)");

    public static FilterDto parse(String filterEntry) {
        if (StringUtils.isBlank(filterEntry)) {
            throw new IncorrectFilterFormatException();
        }

        final Matcher matcher = LOG_PATTERN.matcher(filterEntry);

        if (!matcher.matches()) {
            throw new IncorrectFilterFormatException();
        }

        final String key = matcher.group(1);
        final String value = matcher.group(2);

        return new FilterDto(key, value);
    }
}
