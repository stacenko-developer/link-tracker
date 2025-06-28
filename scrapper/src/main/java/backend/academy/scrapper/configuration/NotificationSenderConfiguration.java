package backend.academy.scrapper.configuration;

import static backend.academy.scrapper.constants.NotificationSenderConstValues.HTTP_MESSAGE_TRANSPORT;
import static backend.academy.scrapper.constants.NotificationSenderConstValues.KAFKA_MESSAGE_TRANSPORT;
import static backend.academy.scrapper.constants.NotificationSenderConstValues.MESSAGE_TRANSPORT_PROPERTY;

import backend.academy.scrapper.sender.alternative.AlternativeHttpNotificationSender;
import backend.academy.scrapper.sender.alternative.AlternativeKafkaNotificationSender;
import backend.academy.scrapper.sender.alternative.AlternativeNotificationSender;
import backend.academy.scrapper.sender.primary.HttpNotificationSender;
import backend.academy.scrapper.sender.primary.KafkaNotificationSender;
import backend.academy.scrapper.sender.primary.NotificationSender;
import backend.academy.scrapper.service.client.BotClientService;
import backend.academy.scrapper.service.client.KafkaClientService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationSenderConfiguration {

    @Configuration
    @ConditionalOnProperty(name = MESSAGE_TRANSPORT_PROPERTY, havingValue = HTTP_MESSAGE_TRANSPORT)
    public static class HttpNotificationSenderConfiguration {

        @Bean
        public AlternativeNotificationSender alternativeNotificationSender(KafkaClientService kafkaClientService) {
            return new AlternativeKafkaNotificationSender(kafkaClientService);
        }

        @Bean
        public NotificationSender notificationSender(
                BotClientService botServiceClient, AlternativeNotificationSender alternativeNotificationSender) {
            return new HttpNotificationSender(botServiceClient, alternativeNotificationSender);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = MESSAGE_TRANSPORT_PROPERTY, havingValue = KAFKA_MESSAGE_TRANSPORT)
    public static class KafkaNotificationSenderConfiguration {

        @Bean
        public AlternativeNotificationSender alternativeNotificationSender(BotClientService botClientService) {
            return new AlternativeHttpNotificationSender(botClientService);
        }

        @Bean
        public NotificationSender notificationSender(
                KafkaClientService kafkaClientService, AlternativeNotificationSender alternativeNotificationSender) {
            return new KafkaNotificationSender(kafkaClientService, alternativeNotificationSender);
        }
    }
}
