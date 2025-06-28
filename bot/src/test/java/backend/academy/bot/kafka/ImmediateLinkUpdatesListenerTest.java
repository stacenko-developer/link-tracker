package backend.academy.bot.kafka;

import static backend.academy.bot.constants.KafkaConstValues.DEAD_LETTER_QUEUE_FORMAT;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doThrow;

import backend.academy.bot.TestConfiguration;
import backend.academy.bot.configuration.BotConfiguration;
import backend.academy.bot.dto.linkUpdate.EventDto;
import backend.academy.bot.dto.linkUpdate.ImmediateLinkUpdate;
import backend.academy.bot.service.BotUpdaterService;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ImmediateLinkUpdatesListenerTest extends TestConfiguration {

    private static final Duration POLL_INTERVAL = Duration.ofMillis(500);
    private static final Duration AT_MOST = Duration.ofSeconds(20);

    private static final Long DEFAULT_ID = 1L;
    private static final URI DEFAULT_URL = URI.create("https://uri.com");
    private static final List<Long> DEFAULT_CHAT_IDS = List.of(1L, 2L, 3L);

    private static final String DEFAULT_EVENT_TYPE = "type";
    private static final String DEFAULT_EVENT_TITLE = "title";
    private static final String DEFAULT_EVENT_USER = "user";
    private static final Long DEFAULT_EVENT_CREATED_AT = 5L;
    private static final Long DEFAULT_EVENT_UPDATED_AT = 7L;
    private static final String DEFAULT_EVENT_TEXT = "text";

    private static final String ORIGINAL_CONSUMER_GROUP_HEADER = "kafka_dlt-original-consumer-group";
    private static final String ORIGINAL_TOPIC_HEADER = "kafka_original-topic";
    private static final String EXCEPTION_CAUSE_HEADER = "kafka_exception-cause-fqcn";
    private static final String EXCEPTION_MESSAGE_HEADER = "kafka_exception-message";

    private static final EventDto DEFAULT_EVENT_DTO = new EventDto(
            DEFAULT_EVENT_TYPE,
            DEFAULT_EVENT_TITLE,
            DEFAULT_EVENT_USER,
            DEFAULT_EVENT_CREATED_AT,
            DEFAULT_EVENT_UPDATED_AT,
            DEFAULT_EVENT_TEXT);

    private static final ImmediateLinkUpdate DEFAULT_LINK_UPDATE =
            new ImmediateLinkUpdate(DEFAULT_ID, DEFAULT_URL, DEFAULT_EVENT_DTO, DEFAULT_CHAT_IDS);

    @MockitoBean
    private BotUpdaterService botUpdaterService;

    @Autowired
    private KafkaTemplate<String, ImmediateLinkUpdate> kafkaTemplate;

    @Autowired
    private BotConfiguration botConfiguration;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Test
    public void listenMessageWithCorrectArguments_ShouldCorrectlyProcess() {
        kafkaTemplate.send(botConfiguration.kafkaTopics().immediateLinkUpdate().name(), DEFAULT_LINK_UPDATE);

        await().pollInterval(POLL_INTERVAL).atMost(AT_MOST).untilAsserted(() -> Mockito.verify(botUpdaterService)
                .immediatelyUpdate(DEFAULT_LINK_UPDATE));
    }

    @Test
    public void listenMessageWithRuntimeExceptionInProcessing_ShouldSendToDeadLetterQueue() {
        int expectedRecordsCount = 1;

        String expectedConsumerGroup = kafkaProperties.getConsumer().getGroupId();
        String expectedOriginalTopic =
                botConfiguration.kafkaTopics().immediateLinkUpdate().name();
        String expectedExceptionName = RuntimeException.class.getSimpleName();
        String expectedExceptionMessage = "exception";
        String dlqTopic = String.format(
                DEAD_LETTER_QUEUE_FORMAT,
                botConfiguration.kafkaTopics().immediateLinkUpdate().name());

        doThrow(new RuntimeException(expectedExceptionMessage))
                .when(botUpdaterService)
                .immediatelyUpdate(DEFAULT_LINK_UPDATE);

        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();

        try (KafkaConsumer<String, ImmediateLinkUpdate> deadLetterQueueConsumer = new KafkaConsumer<>(properties)) {
            deadLetterQueueConsumer.subscribe(List.of(dlqTopic));

            kafkaTemplate.send(
                    botConfiguration.kafkaTopics().immediateLinkUpdate().name(), DEFAULT_LINK_UPDATE);

            await().pollInterval(POLL_INTERVAL).atMost(AT_MOST).untilAsserted(() -> {
                ConsumerRecords<String, ImmediateLinkUpdate> records = deadLetterQueueConsumer.poll(POLL_INTERVAL);

                Assertions.assertEquals(expectedRecordsCount, records.count());

                ConsumerRecord<String, ImmediateLinkUpdate> record =
                        records.iterator().next();

                String actualConsumerGroup = new String(record.headers()
                        .lastHeader(ORIGINAL_CONSUMER_GROUP_HEADER)
                        .value());
                String actualOriginalTopic = new String(
                        record.headers().lastHeader(ORIGINAL_TOPIC_HEADER).value());
                String actualExceptionName = new String(
                        record.headers().lastHeader(EXCEPTION_CAUSE_HEADER).value());
                String actualExceptionMessage = new String(
                        record.headers().lastHeader(EXCEPTION_MESSAGE_HEADER).value());

                Assertions.assertEquals(DEFAULT_LINK_UPDATE, record.value());

                Assertions.assertEquals(expectedConsumerGroup, actualConsumerGroup);
                Assertions.assertEquals(expectedOriginalTopic, actualOriginalTopic);

                Assertions.assertTrue(actualExceptionName.contains(expectedExceptionName));
                Assertions.assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));

                Mockito.verify(botUpdaterService).immediatelyUpdate(DEFAULT_LINK_UPDATE);
            });
        }
    }

    @Test
    public void listenNullMessage_ShouldSendToDeadLetterQueue() {
        String expectedExceptionName = MethodArgumentNotValidException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(null, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullId_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate =
                new ImmediateLinkUpdate(null, DEFAULT_URL, DEFAULT_EVENT_DTO, DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullUrl_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate =
                new ImmediateLinkUpdate(DEFAULT_ID, null, DEFAULT_EVENT_DTO, DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullTgChatIds_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate =
                new ImmediateLinkUpdate(DEFAULT_ID, DEFAULT_URL, DEFAULT_EVENT_DTO, null);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullEventType_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate = new ImmediateLinkUpdate(
                DEFAULT_ID,
                DEFAULT_URL,
                new EventDto(
                        null,
                        DEFAULT_EVENT_TITLE,
                        DEFAULT_EVENT_USER,
                        DEFAULT_EVENT_CREATED_AT,
                        DEFAULT_EVENT_UPDATED_AT,
                        DEFAULT_EVENT_TEXT),
                DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullEventUser_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate = new ImmediateLinkUpdate(
                DEFAULT_ID,
                DEFAULT_URL,
                new EventDto(
                        DEFAULT_EVENT_TYPE,
                        DEFAULT_EVENT_TITLE,
                        null,
                        DEFAULT_EVENT_CREATED_AT,
                        DEFAULT_EVENT_UPDATED_AT,
                        DEFAULT_EVENT_TEXT),
                DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullCreatedAt_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate = new ImmediateLinkUpdate(
                DEFAULT_ID,
                DEFAULT_URL,
                new EventDto(
                        DEFAULT_EVENT_TYPE,
                        DEFAULT_EVENT_TITLE,
                        DEFAULT_EVENT_USER,
                        null,
                        DEFAULT_EVENT_UPDATED_AT,
                        DEFAULT_EVENT_TEXT),
                DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    @Test
    public void listenMessageWithNullUpdatedAt_ShouldSendToDeadLetterQueue() {
        ImmediateLinkUpdate incorrectImmediateLinkUpdate = new ImmediateLinkUpdate(
                DEFAULT_ID,
                DEFAULT_URL,
                new EventDto(
                        DEFAULT_EVENT_TYPE,
                        DEFAULT_EVENT_TITLE,
                        DEFAULT_EVENT_USER,
                        DEFAULT_EVENT_CREATED_AT,
                        null,
                        DEFAULT_EVENT_TEXT),
                DEFAULT_CHAT_IDS);
        String expectedExceptionName = ConstraintViolationException.class.getSimpleName();

        listenMessageWithIncorrectArgumentsProcess(incorrectImmediateLinkUpdate, expectedExceptionName);
    }

    private void listenMessageWithIncorrectArgumentsProcess(
            ImmediateLinkUpdate incorrectImmediateLinkUpdate, String expectedExceptionName) {
        int expectedRecordsCount = 1;

        String expectedOriginalTopic =
                botConfiguration.kafkaTopics().immediateLinkUpdate().name();

        String dlqTopic = String.format(
                DEAD_LETTER_QUEUE_FORMAT,
                botConfiguration.kafkaTopics().immediateLinkUpdate().name());

        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();

        try (KafkaConsumer<String, ImmediateLinkUpdate> deadLetterQueueConsumer = new KafkaConsumer<>(properties)) {
            deadLetterQueueConsumer.subscribe(List.of(dlqTopic));

            kafkaTemplate.send(
                    botConfiguration.kafkaTopics().immediateLinkUpdate().name(), incorrectImmediateLinkUpdate);

            await().pollInterval(POLL_INTERVAL).atMost(AT_MOST).untilAsserted(() -> {
                ConsumerRecords<String, ImmediateLinkUpdate> records = deadLetterQueueConsumer.poll(POLL_INTERVAL);

                Assertions.assertEquals(expectedRecordsCount, records.count());

                ConsumerRecord<String, ImmediateLinkUpdate> record =
                        records.iterator().next();

                String actualOriginalTopic = new String(
                        record.headers().lastHeader(ORIGINAL_TOPIC_HEADER).value());
                String actualExceptionName = new String(
                        record.headers().lastHeader(EXCEPTION_CAUSE_HEADER).value());

                Assertions.assertEquals(incorrectImmediateLinkUpdate, record.value());
                Assertions.assertEquals(expectedOriginalTopic, actualOriginalTopic);

                Assertions.assertTrue(actualExceptionName.contains(expectedExceptionName));
            });
        }
    }
}
