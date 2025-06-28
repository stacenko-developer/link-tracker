package backend.academy.scrapper.exception.chat;

import backend.academy.common.exception.NotFoundException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;

public class ChatNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 2316298341098038019L;

    public ChatNotFoundException(Long chatId) {
        super(
                ExceptionMessageValues.CHAT_NOT_FOUND_EXCEPTION_MESSAGE,
                String.format(ExceptionDescriptionValues.CHAT_NOT_FOUND_EXCEPTION_DESCRIPTION, chatId));
    }
}
