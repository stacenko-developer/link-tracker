package backend.academy.scrapper.configuration;

import static backend.academy.scrapper.constants.NotificationSenderConstValues.KAFKA_MESSAGE_TRANSPORT;
import static backend.academy.scrapper.constants.NotificationSenderConstValues.MESSAGE_TRANSPORT_PROPERTY;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = MESSAGE_TRANSPORT_PROPERTY, havingValue = KAFKA_MESSAGE_TRANSPORT)
public class KafkaConfiguration {

    private static final int NUM_PARTITIONS = 1;
    private static final short REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic immediateLinkUpdateTopic(ScrapperConfig config) {
        return new NewTopic(config.kafkaTopics().immediateLinkUpdate(), NUM_PARTITIONS, REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic digestLinkUpdateTopic(ScrapperConfig config) {
        return new NewTopic(config.kafkaTopics().digestLinkUpdate(), NUM_PARTITIONS, REPLICATION_FACTOR);
    }
}
