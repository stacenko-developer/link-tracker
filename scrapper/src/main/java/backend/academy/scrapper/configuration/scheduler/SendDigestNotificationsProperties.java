package backend.academy.scrapper.configuration.scheduler;

import static backend.academy.scrapper.constants.ConfigurationConstants.SEND_DIGEST_NOTIFICATIONS_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = SEND_DIGEST_NOTIFICATIONS_PROPERTY, ignoreUnknownFields = false)
public record SendDigestNotificationsProperties(@NotBlank String startTimeCron, @NotNull Integer batchSize) {}
