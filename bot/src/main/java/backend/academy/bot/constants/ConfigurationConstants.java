package backend.academy.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationConstants {

    public static final String MESSAGE_FILE = "classpath:message.yaml";

    public static final String APP_PROPERTY = "app";
    public static final String UPDATE_LINK_PROPERTY = "update-link";

    public static final String STACKOVERFLOW_COMMENT_PROPERTY = UPDATE_LINK_PROPERTY + ".stackoverflow-comment";
    public static final String STACKOVERFLOW_ANSWER_PROPERTY = UPDATE_LINK_PROPERTY + ".stackoverflow-answer";
    public static final String GITHUB_ISSUE_PROPERTY = UPDATE_LINK_PROPERTY + ".github-issue";
    public static final String GITHUB_PULL_REQUEST_PROPERTY = UPDATE_LINK_PROPERTY + ".github-pull-request";

    public static final String DEFAULT_EVENT_PROPERTY = UPDATE_LINK_PROPERTY + ".default-event";
    public static final String DIGEST_PROPERTY = UPDATE_LINK_PROPERTY + ".digest";

    public static final String CLIENT_PROPERTY = "client";
    public static final String SCRAPPER_CLIENT_PROPERTY = CLIENT_PROPERTY + ".scrapper";

    public static final String COMMAND_PROPERTY = "command";
    public static final String ERROR_MESSAGE_PROPERTY = COMMAND_PROPERTY + ".error-message";
    public static final String HELP_COMMAND_PROPERTY = COMMAND_PROPERTY + ".help";
    public static final String LIST_COMMAND_PROPERTY = COMMAND_PROPERTY + ".list";
    public static final String NOTIFICATION_MODE_COMMAND_PROPERTY = COMMAND_PROPERTY + ".notification-mode";
    public static final String START_COMMAND_PROPERTY = COMMAND_PROPERTY + ".start";
    public static final String TRACK_COMMAND_PROPERTY = COMMAND_PROPERTY + ".track";
    public static final String UNTRACK_COMMAND_PROPERTY = COMMAND_PROPERTY + ".untrack";

    public static final String RATE_LIMITER_PROPERTY = "rate-limiter";
}
