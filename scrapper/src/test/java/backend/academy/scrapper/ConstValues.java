package backend.academy.scrapper;

import backend.academy.common.dto.ApiErrorResponse;
import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.client.bot.dto.LinkInfo;
import backend.academy.scrapper.client.github.dto.ActorDto;
import backend.academy.scrapper.client.github.dto.GithubEventDto;
import backend.academy.scrapper.client.github.dto.GithubMetaInformationDto;
import backend.academy.scrapper.client.github.dto.PayloadDto;
import backend.academy.scrapper.client.stackoverflow.dto.ItemDto;
import backend.academy.scrapper.client.stackoverflow.dto.OwnerDto;
import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import backend.academy.scrapper.dto.request.link.AddLinkRequest;
import backend.academy.scrapper.linkTracker.dto.EventDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public class ConstValues {

    public static final URI DEFAULT_URL = URI.create("https://github.com/stacenko-developer/hangman-game");
    public static final Long DEFAULT_CHAT_ID = 1L;
    public static final Long DEFAULT_LINK_ID = 1L;
    public static final Long DEFAULT_QUESTION_ID = 1L;
    public static final String DEFAULT_SCENARIO_NAME = "RetryScenario";

    public static final String DEFAULT_OWNER = "owner";
    public static final String DEFAULT_REPO = "repo";

    public static final List<String> DEFAULT_AVAILABLE_URLS =
            List.of("https://github.com", "https://stackoverflow.com");
    public static final List<String> DEFAULT_TAGS = List.of("тэг1", "тэг2");

    public static final String DEFAULT_USER_FILTER_KEY = "user";
    public static final String DEFAULT_EVENT_FILTER_KEY = "event";
    public static final List<String> DEFAULT_FILTERS = List.of(
            String.format("%s:event", DEFAULT_EVENT_FILTER_KEY), String.format("%s:viktor", DEFAULT_USER_FILTER_KEY));
    public static final List<String> DEFAULT_AVAILABLE_FILTERS = List.of("фильтр1", "фильтр2");

    public static final AddLinkRequest DEFAULT_ADD_LINK_REQUEST =
            new AddLinkRequest(DEFAULT_URL, DEFAULT_TAGS, DEFAULT_FILTERS);

    public static final EventDto DEFAULT_EVENT_DTO = new EventDto("type", "title", "user", 1L, 1L, "text");

    public static final ImmediateLinkUpdate DEFAULT_IMMEDIATE_LINK_UPDATE =
            new ImmediateLinkUpdate(1L, DEFAULT_URL, DEFAULT_EVENT_DTO, List.of(DEFAULT_CHAT_ID));
    public static final DigestLinkUpdate DEFAULT_DIGEST_LINK_UPDATE =
            new DigestLinkUpdate(DEFAULT_CHAT_ID, List.of(new LinkInfo(DEFAULT_URL, DEFAULT_EVENT_DTO)));

    public static final List<GithubEventDto> DEFAULT_EVENTS = List.of(
            new GithubEventDto(
                    new PayloadDto(
                            null,
                            new GithubMetaInformationDto(
                                    "PullRequest Title",
                                    "Pull Request Description",
                                    OffsetDateTime.MIN,
                                    OffsetDateTime.MIN)),
                    OffsetDateTime.MIN,
                    "PullRequest",
                    new ActorDto("viktor")),
            new GithubEventDto(
                    new PayloadDto(
                            new GithubMetaInformationDto(
                                    "Issue Title", "Issues Description", OffsetDateTime.MIN, OffsetDateTime.MIN),
                            null),
                    OffsetDateTime.MIN,
                    "Issue",
                    new ActorDto("sergey")));
    public static final List<ItemDto> DEFAULT_QUESTION_INFORMATION =
            List.of(new ItemDto(0L, 0L, new OwnerDto("viktor"), "Question title", "Question body"));
    public static final StackoverflowResponseDto DEFAULT_STACKOVERFLOW_RESPONSE_DTO =
            new StackoverflowResponseDto(DEFAULT_QUESTION_INFORMATION);

    public static final ApiErrorResponse DEFAULT_API_ERROR_RESPONSE = new ApiErrorResponse(
            "описание ошибки",
            "код ошибки",
            "название исключения",
            "сообщение",
            List.of(
                    "java.lang.RuntimeException: Исключение для примера",
                    "at StackTraceExample.throwException(StackTraceExample.java:11)",
                    "at StackTraceExample.main(StackTraceExample.java:6)"));

    public static final String UPDATES_API_BASE_URL = "/updates";
    public static final String IMMEDIATE_UPDATE_URL = String.format("%s/immediate", UPDATES_API_BASE_URL);
    public static final String DIGEST_UPDATE_URL = String.format("%s/digest", UPDATES_API_BASE_URL);

    public static final String GET_EVENTS_URL = String.format("/repos/%s/%s/events", DEFAULT_OWNER, DEFAULT_REPO);
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    public static final String GET_ANSWERS_URL_FORMAT = "/questions/%s/answers";
    public static final String GET_COMMENTS_URL_FORMAT = "/questions/%s/comments";
    public static final String GET_QUESTION_INFORMATION_URL_FORMAT = "/questions/%s";

    public static final String SITE_QUERY_PARAM_NAME = "site";
    public static final String FILTER_QUERY_PARAM_NAME = "filter";
    public static final String KEY_QUERY_PARAM_NAME = "key";
    public static final String ACCESS_TOKEN_QUERY_PARAM_NAME = "access_token";

    public static final String SITE_QUERY_PARAM_VALUE = "stackoverflow";
    public static final String FILTER_QUERY_PARAM_VALUE = "withbody";
}
