package backend.academy.scrapper.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class APIConstValues {

    public static final String LINK_UPDATE_API_BASE_URL = "/updates";
    public static final String IMMEDIATE_UPDATE_URL = LINK_UPDATE_API_BASE_URL + "/immediate";
    public static final String DIGEST_UPDATE_URL = LINK_UPDATE_API_BASE_URL + "/digest";

    public static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String GET_REPOSITORIES_URL = "/repos/{owner}/{repository}/events";

    public static final String STACKOVERFLOW_API_BASE_URL = "/questions/{questionId}";
    public static final String STACKOVERFLOW_QUERY_PARAMS =
            "?site=stackoverflow&filter=withbody&key={key}&access_token={accessToken}";

    public static final String STACKOVERFLOW_ANSWERS_URL =
            STACKOVERFLOW_API_BASE_URL + "/answers" + STACKOVERFLOW_QUERY_PARAMS;
    public static final String STACKOVERFLOW_COMMENTS_URL =
            STACKOVERFLOW_API_BASE_URL + "/comments" + STACKOVERFLOW_QUERY_PARAMS;
    public static final String STACKOVERFLOW_QUESTION_INFORMATION_URL =
            STACKOVERFLOW_API_BASE_URL + STACKOVERFLOW_QUERY_PARAMS;

    public static final String LINKS_API_BASE_URL = "/links";
    public static final String SEARCH_LINKS_URL = "/search";

    public static final String TG_CHAT_API_BASE_URL = "/tg-chat";
    public static final String TG_CHAT_API_FULL_URL = TG_CHAT_API_BASE_URL + "/{id}";

    public static final String TG_CHAT_SETTINGS_API_BASE_URL = "/tg-chat-settings";
    public static final String GET_NOTIFICATION_MODES_URL = "/notification-mode";
}
