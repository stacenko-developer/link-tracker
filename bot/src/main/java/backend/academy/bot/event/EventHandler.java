package backend.academy.bot.event;

import static backend.academy.bot.constants.ConstValues.DATE_TIME_FORMATTER;

import backend.academy.bot.dto.linkUpdate.EventDto;
import backend.academy.common.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class EventHandler {

    private static final String NOT_SPECIFIED = "Не указано";

    private static final String CREATE_ACTION = "Создание";
    private static final String EDIT_ACTION = "Редактирование";

    private static final int MAX_LENGTH = 200;
    private static final int START_INDEX = 0;

    public abstract String getMessageFormat();

    public abstract String getName();

    public String getMessage(EventDto eventDto) {
        return String.format(
                getMessageFormat(),
                getValueOrDefault(eventDto.title()),
                eventDto.user(),
                DateTimeUtils.toUtc(eventDto.updatedAt()).format(DATE_TIME_FORMATTER),
                getActionType(eventDto.createdAt(), eventDto.updatedAt()),
                getAbbreviatedText(getValueOrDefault(eventDto.text())));
    }

    private String getValueOrDefault(String value) {
        return StringUtils.isNotBlank(value) ? value : NOT_SPECIFIED;
    }

    private String getActionType(Long createdAt, Long updatedAt) {
        return createdAt.equals(updatedAt) ? CREATE_ACTION : EDIT_ACTION;
    }

    private String getAbbreviatedText(String text) {
        return text.substring(START_INDEX, Math.min(text.length(), MAX_LENGTH));
    }
}
