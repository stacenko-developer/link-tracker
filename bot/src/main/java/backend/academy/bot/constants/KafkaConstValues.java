package backend.academy.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaConstValues {

    public static final String DEAD_LETTER_QUEUE_FORMAT = "%s_dlq";
    public static final String DEFAULT_DLT_TOPIC_VALUE = "_dlq";
    public static final String DEFAULT_MAX_ATTEMPTS = "1";

    public static final String IMMEDIATELY_UPDATE_CONTAINER_FACTORY = "validatedImmediateLinkUpdateContainerFactory";
    public static final String DIGEST_UPDATE_CONTAINER_FACTORY = "validatedDigestLinkUpdateContainerFactory";

    public static final String IMMEDIATE_UPDATE_TOPIC = "${app.kafka-topics.immediate-link-update.name}";
    public static final String DIGEST_UPDATE_TOPIC = "${app.kafka-topics.digest-link-update.name}";
    public static final String GROUP_ID = "${spring.kafka.consumer.group-id}";
}
