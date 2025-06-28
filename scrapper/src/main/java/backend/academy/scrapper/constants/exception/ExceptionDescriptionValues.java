package backend.academy.scrapper.constants.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionDescriptionValues {
    public static final String CHAT_HAS_ALREADY_REGISTERED_EXCEPTION_DESCRIPTION =
            "Чат с идентификатором %d уже есть в системе";
    public static final String CHAT_NOT_FOUND_EXCEPTION_DESCRIPTION = "Чат с идентификатором %d не найден в системе";
    public static final String LINK_HAS_ALREADY_ADDED_EXCEPTION_DESCRIPTION =
            "Ссылка %s для чата с идентификатором %d уже есть в системе";
    public static final String LINK_NOT_FOUND_EXCEPTION_DESCRIPTION =
            "Ссылка %s для чата с идентификатором %d отсутствует в системе";
    public static final String LINK_NOT_SUPPORTED_EXCEPTION_DESCRIPTION =
            "Ссылка %s не поддерживается для отслеживания";
    public static final String NOTIFICATION_MODE_NOT_SUPPORTED_EXCEPTION_DESCRIPTION =
            "Режим отправки уведомления %s не поддерживается для использования";
    public static final String FILTER_NOT_SUPPORTED_EXCEPTION_DESCRIPTION =
            "Фильтр %s не поддерживается для типа ссылок %s. Допустимые фильтры: %n%s";
    public static final String INCORRECT_FILTER_FORMAT_EXCEPTION_DESCRIPTION =
            "Фильтр должен соответвовать формату: ключ:значение";

    public static final String AVAILABLE_LINKS_DESCRIPTION = "Список поддерживаемых ссылок: %s";
    public static final String AVAILABLE_NOTIFICATION_MODES_DESCRIPTION =
            "Список поддерживаемых режимов отправки уведомлений: %s";
}
