package backend.academy.scrapper.exception.chat;

import backend.academy.common.exception.BadRequestException;
import backend.academy.scrapper.constants.exception.ExceptionDescriptionValues;
import backend.academy.scrapper.constants.exception.ExceptionMessageValues;
import java.io.Serial;

public class ChatHasAlreadyRegisteredException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 4956403714894322442L;

    public ChatHasAlreadyRegisteredException(Long chatId) {
        super(
                ExceptionMessageValues.CHAT_HAS_ALREADY_REGISTERED_EXCEPTION_MESSAGE,
                String.format(ExceptionDescriptionValues.CHAT_HAS_ALREADY_REGISTERED_EXCEPTION_DESCRIPTION, chatId));
    }
}
