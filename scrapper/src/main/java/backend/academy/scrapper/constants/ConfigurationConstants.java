package backend.academy.scrapper.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConfigurationConstants {

    public static final String APP_PROPERTY = "app";

    public static final String CLIENT_PROPERTY = "client";
    public static final String BOT_CLIENT_PROPERTY = CLIENT_PROPERTY + ".bot";
    public static final String GITHUB_CLIENT_PROPERTY = CLIENT_PROPERTY + ".github";
    public static final String STACKOVERFLOW_CLIENT_PROPERTY = CLIENT_PROPERTY + ".stackoverflow";

    public static final String SCHEDULER_PROPERTY = "scheduler";
    public static final String LINK_TRACKING_PROPERTY = SCHEDULER_PROPERTY + ".track-updating-links";
    public static final String ORPHAN_REMOVE_PROPERTY = SCHEDULER_PROPERTY + ".orphan-remove";
    public static final String SEND_DIGEST_NOTIFICATIONS_PROPERTY = SCHEDULER_PROPERTY + ".send-digest-notifications";

    public static final String RATE_LIMITER_PROPERTY = "rate-limiter";
}
