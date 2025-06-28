package backend.academy.scrapper.linkTracker;

import backend.academy.scrapper.dto.FilterDto;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public abstract class LinkTracker {

    private static final String EVENT_FILTER = "event";
    private static final String USER_FILTER = "user";

    private static final List<String> FILTERS = List.of(EVENT_FILTER, USER_FILTER);

    public abstract Pattern getUrlPattern();

    public abstract String getLinkType();

    public List<String> getFilters() {
        return FILTERS;
    }

    public abstract List<EventDto> track(URI url);

    public List<EventDto> getFilteredEvents(List<EventDto> events, List<FilterDto> filters) {
        if (filters.isEmpty()) {
            return events;
        }

        Predicate<EventDto> combinedPredicate = filters.stream()
                .map(this::createPredicate)
                .reduce(Predicate::and)
                .orElse(event -> true);

        return events.stream().filter(combinedPredicate).toList();
    }

    public boolean isSupports(URI url) {
        return url != null && getUrlPattern().matcher(url.toString()).matches();
    }

    public boolean isFilterSupport(String key) {
        return StringUtils.isNotBlank(key)
                && getFilters().stream().anyMatch(filter -> filter.equalsIgnoreCase(key.trim()));
    }

    private Predicate<EventDto> createPredicate(FilterDto filter) {
        return switch (filter.key()) {
            case EVENT_FILTER -> createFieldPredicate(EventDto::title, filter.value());
            case USER_FILTER -> createFieldPredicate(EventDto::user, filter.value());
            default -> event -> true;
        };
    }

    private Predicate<EventDto> createFieldPredicate(Function<EventDto, String> fieldExtractor, String value) {
        return event -> {
            String fieldValue = fieldExtractor.apply(event);
            return StringUtils.isBlank(fieldValue) || !fieldValue.equalsIgnoreCase(value);
        };
    }
}
