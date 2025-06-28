package backend.academy.scrapper.configuration;

import static backend.academy.scrapper.constants.ConfigurationConstants.APP_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = APP_PROPERTY, ignoreUnknownFields = false)
public record ScrapperConfig(
        @NotEmpty String githubToken,
        @NotNull StackOverflowProperties stackOverflow,
        @NotNull AccessType accessType,
        @NotNull MessageTransport messageTransport,
        @NotNull Cache cache,
        @NotNull KafkaTopics kafkaTopics,
        @NotNull ResilienceInstances resilienceInstances) {

    public record StackOverflowProperties(@NotEmpty String key, @NotEmpty String accessToken) {}

    public record Cache(@NotBlank String botTrackingLinks, @NotBlank String scrapperTrackingLinks) {}

    public record KafkaTopics(@NotBlank String immediateLinkUpdate, @NotBlank String digestLinkUpdate) {}

    public record ResilienceInstances(
            @NotNull DefaultInstanceInfo github,
            @NotNull DefaultInstanceInfo stackoverflow,
            @NotNull DefaultInstanceInfo httpNotificationSender,
            @NotNull KafkaInstanceInfo kafkaNotificationSender) {

        public record DefaultInstanceInfo(@NotBlank String circuitBreaker, @NotBlank String retry) {}

        public record KafkaInstanceInfo(@NotBlank String circuitBreaker) {}
    }

    public enum AccessType {
        SQL,
        ORM
    }

    public enum MessageTransport {
        HTTP,
        KAFKA
    }
}
