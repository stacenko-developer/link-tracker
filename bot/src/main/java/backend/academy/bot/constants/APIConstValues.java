package backend.academy.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class APIConstValues {

    public static final String LINKS_API_BASE_URL = "/links";
    public static final String LINKS_SEARCH_URL = LINKS_API_BASE_URL + "/search";

    public static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";
    public static final String OPERATION_WITH_CHAT_URL = "/tg-chat/{id}";

    public static final String CHAT_SETTINGS_API_BASE_URL = "/tg-chat-settings";
    public static final String NOTIFICATION_MODE_URL = CHAT_SETTINGS_API_BASE_URL + "/notification-mode";

    public static final String UPDATES_API_BASE_URL = "/updates";
    public static final String DIGEST_UPDATE_URL = "/digest";
    public static final String IMMEDIATE_UPDATE_URL = "/immediate";
}
