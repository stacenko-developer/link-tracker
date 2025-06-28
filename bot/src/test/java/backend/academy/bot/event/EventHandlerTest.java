package backend.academy.bot.event;

import static backend.academy.bot.ConstValues.DEFAULT_EVENT_TYPE;
import static backend.academy.bot.ConstValues.DEFAULT_TEXT;
import static backend.academy.bot.ConstValues.DEFAULT_TITLE;
import static backend.academy.bot.ConstValues.DEFAULT_USER;
import static backend.academy.bot.constants.ConstValues.DATE_TIME_FORMATTER;

import backend.academy.bot.dto.linkUpdate.EventDto;
import backend.academy.common.utils.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class EventHandlerTest {

    protected abstract EventHandler getEventHandler();

    protected static final String MESSAGE =
            """
            🚀 Событие
            🔹 Название Issue: %s
            🔹 Автор: %s
            🔹 Дата события в UTC: %s
            🔹 Тип действия: %s
            🔹 Описание (первые 200 символов): %s
            """;

    @Test
    public void getMessageForCorrectEventWithCreateAction_ShouldReturnMessage() {
        String creationActionType = "Создание";
        Long createdAt = 10L;
        Long updatedAt = 10L;
        EventDto eventDto =
                new EventDto(DEFAULT_EVENT_TYPE, DEFAULT_TITLE, DEFAULT_USER, createdAt, updatedAt, DEFAULT_TEXT);

        String[] actualMessage = getEventHandler().getMessage(eventDto).split("\n");

        Assertions.assertTrue(actualMessage[1].contains(DEFAULT_TITLE));
        Assertions.assertTrue(actualMessage[2].contains(DEFAULT_USER));
        Assertions.assertTrue(
                actualMessage[3].contains(DateTimeUtils.toUtc(updatedAt).format(DATE_TIME_FORMATTER)));
        Assertions.assertTrue(actualMessage[4].contains(creationActionType));
        Assertions.assertTrue(actualMessage[5].contains(DEFAULT_TEXT));
    }

    @Test
    public void getMessageForCorrectEventWithEditAction_ShouldReturnMessage() {
        String editActionType = "Редактирование";
        Long createdAt = 10L;
        Long updatedAt = 20L;
        EventDto eventDto =
                new EventDto(DEFAULT_EVENT_TYPE, DEFAULT_TITLE, DEFAULT_USER, createdAt, updatedAt, DEFAULT_TEXT);

        String[] actualMessage = getEventHandler().getMessage(eventDto).split("\n");

        Assertions.assertTrue(actualMessage[1].contains(DEFAULT_TITLE));
        Assertions.assertTrue(actualMessage[2].contains(DEFAULT_USER));
        Assertions.assertTrue(
                actualMessage[3].contains(DateTimeUtils.toUtc(updatedAt).format(DATE_TIME_FORMATTER)));
        Assertions.assertTrue(actualMessage[4].contains(editActionType));
        Assertions.assertTrue(actualMessage[5].contains(DEFAULT_TEXT));
    }
}
