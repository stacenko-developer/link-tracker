package backend.academy.scrapper.constants.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessageValues {
    public static final String CHAT_NOT_FOUND_EXCEPTION_MESSAGE = "Чат не существует";
    public static final String LINK_NOT_FOUND_EXCEPTION_MESSAGE = "Ссылка не существует";
    public static final String CHAT_HAS_ALREADY_REGISTERED_EXCEPTION_MESSAGE = "Повторная регистрация чата";
    public static final String LINK_HAS_ALREADY_ADDED_EXCEPTION_MESSAGE = "Повторное добавление ссылки";
    public static final String LINK_NOT_SUPPORTED_EXCEPTION_MESSAGE = "Ссылка не поддерживается";
    public static final String FILTER_NOT_SUPPORTED_EXCEPTION_MESSAGE = "Фильтр не поддерживается";
    public static final String INCORRECT_FILTER_FORMAT_EXCEPTION_MESSAGE = "Некорректный формат фильтра";
    public static final String NOTIFICATION_MODE_NOT_SUPPORTED_EXCEPTION_MESSAGE =
            "Режим отправки уведомления не поддерживается";
}
