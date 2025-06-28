package backend.academy.bot;

import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import backend.academy.bot.client.scrapper.dto.request.AddLinkRequest;
import backend.academy.bot.client.scrapper.dto.request.ChatSettingsRequest;
import backend.academy.bot.client.scrapper.dto.request.FindUserLinksRequest;
import backend.academy.bot.client.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.bot.client.scrapper.dto.response.ChatResponse;
import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import backend.academy.common.dto.ApiErrorResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ConstValues {

    public static final String DEFAULT_EVENT_TYPE = "Тип события";
    public static final String DEFAULT_TITLE = "Название события";
    public static final String DEFAULT_USER = "пользователь";
    public static final String DEFAULT_TEXT = "текст уведомления";

    public static final long DEFAULT_TG_CHAT_ID = 1;
    public static final URI DEFAULT_URL = URI.create("https://github.com/stacenko-developer/Patterns");
    public static final String DEFAULT_SCENARIO_NAME = "RetryScenario";

    public static final FindUserLinksRequest DEFAULT_FIND_USER_LINKS_REQUEST =
            new FindUserLinksRequest(DEFAULT_TG_CHAT_ID, null);
    public static final AddLinkRequest DEFAULT_ADD_LINK_REQUEST =
            new AddLinkRequest(DEFAULT_URL, new ArrayList<>(), new ArrayList<>());
    public static final RemoveLinkRequest DEFAULT_REMOVE_LINK_REQUEST = new RemoveLinkRequest(DEFAULT_URL);

    public static final String DEFAULT_NOTIFICATION_MODE_CODE = "IMMEDIATE";
    public static final ChatSettingsRequest DEFAULT_CHAT_SETTINGS_REQUEST =
            new ChatSettingsRequest(DEFAULT_NOTIFICATION_MODE_CODE);

    public static final NotificationModeDto DEFAULT_NOTIFICATION_MODE_DTO =
            new NotificationModeDto(DEFAULT_NOTIFICATION_MODE_CODE, "title", "description");
    public static final List<NotificationModeDto> DEFAULT_NOTIFICATION_MODES = List.of(DEFAULT_NOTIFICATION_MODE_DTO);

    public static final ChatResponse DEFAULT_CHAT_RESPONSE =
            new ChatResponse(DEFAULT_TG_CHAT_ID, DEFAULT_NOTIFICATION_MODE_DTO);

    public static final ApiErrorResponse DEFAULT_API_ERROR_RESPONSE = new ApiErrorResponse(
            "описание ошибки",
            "код ошибки",
            "название исключения",
            "сообщение",
            List.of(
                    "java.lang.RuntimeException: Исключение для примера",
                    "at StackTraceExample.throwException(StackTraceExample.java:11)",
                    "at StackTraceExample.main(StackTraceExample.java:6)"));

    public static final LinkResponse DEFAULT_LINK_RESPONSE =
            new LinkResponse(1L, DEFAULT_URL, List.of("тэг1", "тэг2"), List.of("фильтр1", "фильтр2"));
    public static final List<LinkResponse> DEFAULT_LINKS = List.of(DEFAULT_LINK_RESPONSE);
    public static final ListLinksResponse DEFAULT_LIST_LINKS_RESPONSE =
            new ListLinksResponse(DEFAULT_LINKS, DEFAULT_LINKS.size());

    public static final String LINKS_API_BASE_URL = "/links";
    public static final String GET_ALL_USER_TRACKING_LINKS_URL = String.format("%s/search", LINKS_API_BASE_URL);

    public static final String CHAT_API_BASE_URL = "/tg-chat";
    public static final String CHAT_ID_HEADER_NAME = "Tg-Chat-Id";

    public static final String CHAT_SETTINGS_API_BASE_URL = "/tg-chat-settings";
    public static final String GET_NOTIFICATION_MODES_URL =
            String.format("%s/notification-mode", CHAT_SETTINGS_API_BASE_URL);

    public static final String TEXT_KEY = "text";
}
