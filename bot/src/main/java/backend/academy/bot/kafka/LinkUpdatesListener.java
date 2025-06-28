package backend.academy.bot.kafka;

import static backend.academy.bot.constants.KafkaConstValues.DEFAULT_DLT_TOPIC_VALUE;
import static backend.academy.bot.constants.KafkaConstValues.DEFAULT_MAX_ATTEMPTS;
import static backend.academy.bot.constants.KafkaConstValues.DIGEST_UPDATE_CONTAINER_FACTORY;
import static backend.academy.bot.constants.KafkaConstValues.DIGEST_UPDATE_TOPIC;
import static backend.academy.bot.constants.KafkaConstValues.GROUP_ID;
import static backend.academy.bot.constants.KafkaConstValues.IMMEDIATELY_UPDATE_CONTAINER_FACTORY;
import static backend.academy.bot.constants.KafkaConstValues.IMMEDIATE_UPDATE_TOPIC;

import backend.academy.bot.dto.linkUpdate.DigestLinkUpdate;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import backend.academy.bot.service.BotUpdaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkUpdatesListener {
    private final BotUpdaterService botUpdaterService;

    @KafkaListener(
            topics = IMMEDIATE_UPDATE_TOPIC,
            groupId = GROUP_ID,
            containerFactory = IMMEDIATELY_UPDATE_CONTAINER_FACTORY)
    @RetryableTopic(
            attempts = DEFAULT_MAX_ATTEMPTS,
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            dltTopicSuffix = DEFAULT_DLT_TOPIC_VALUE)
    public void listenImmediatelyUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        botUpdaterService.immediatelyUpdate(immediateLinkUpdate);
    }

    @KafkaListener(topics = DIGEST_UPDATE_TOPIC, groupId = GROUP_ID, containerFactory = DIGEST_UPDATE_CONTAINER_FACTORY)
    @RetryableTopic(
            attempts = DEFAULT_MAX_ATTEMPTS,
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            dltTopicSuffix = DEFAULT_DLT_TOPIC_VALUE)
    public void listenDigestUpdate(DigestLinkUpdate digestLinkUpdate) {
        botUpdaterService.digestUpdate(digestLinkUpdate);
    }
}
