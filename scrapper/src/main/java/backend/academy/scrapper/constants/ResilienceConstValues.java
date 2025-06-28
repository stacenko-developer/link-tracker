package backend.academy.scrapper.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResilienceConstValues {

    public static final String GITHUB_CIRCUIT_BREAKER = "${app.resilience-instances.github.circuit-breaker}";
    public static final String GITHUB_RETRY = "${app.resilience-instances.github.retry}";
    public static final String DEFAULT_FALLBACK_METHOD_NAME = "fallback";

    public static final String STACKOVERFLOW_CIRCUIT_BREAKER =
            "${app.resilience-instances.stackoverflow.circuit-breaker}";
    public static final String STACKOVERFLOW_RETRY = "${app.resilience-instances.stackoverflow.retry}";

    public static final String HTTP_NOTIFICATION_SENDER_CIRCUIT_BREAKER =
            "${app.resilience-instances.http-notification-sender.circuit-breaker}";
    public static final String HTTP_NOTIFICATION_SENDER_RETRY =
            "${app.resilience-instances.http-notification-sender.retry}";

    public static final String KAFKA_NOTIFICATION_SENDER_CIRCUIT_BREAKER =
            "${app.resilience-instances.kafka-notification-sender.circuit-breaker}";

    public static final String IMMEDIATE_UPDATE_EXCEPTION_FALLBACK_MESSAGE = "exception in immediate fallback method: ";
    public static final String DIGEST_UPDATE_EXCEPTION_FALLBACK_MESSAGE = "exception in digest fallback method: ";

    public static final String SEND_IMMEDIATE_UPDATE_FALLBACK_METH0D = "sendImmediateLinkUpdateFallback";
    public static final String SEND_DIGEST_UPDATE_FALLBACK_METH0D = "sendDigestLinkUpdateFallback";
}
