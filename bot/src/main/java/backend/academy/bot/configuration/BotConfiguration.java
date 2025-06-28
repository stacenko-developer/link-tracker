package backend.academy.bot.configuration;

import static backend.academy.bot.constants.ConfigurationConstants.APP_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = APP_PROPERTY, ignoreUnknownFields = false)
public record BotConfiguration(
        @NotBlank String telegramToken,
        @NotNull Cache cache,
        @NotNull KafkaTopics kafkaTopics,
        @NotNull ResilienceInstances resilienceInstances) {

    public record Cache(@NotBlank String botTrackingLinks) {}

    public record KafkaTopics(@NotNull TopicInfo immediateLinkUpdate, @NotNull TopicInfo digestLinkUpdate) {

        public record TopicInfo(@NotBlank String name, @NotBlank String valueType) {}
    }

    public record ResilienceInstances(@NotNull InstanceInfo scrapperService) {

        public record InstanceInfo(@NotBlank String circuitBreaker, @NotBlank String retry) {}
    }
}
