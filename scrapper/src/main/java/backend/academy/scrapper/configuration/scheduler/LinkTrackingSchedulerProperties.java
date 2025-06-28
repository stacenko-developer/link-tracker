package backend.academy.scrapper.configuration.scheduler;

import static backend.academy.scrapper.constants.ConfigurationConstants.LINK_TRACKING_PROPERTY;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = LINK_TRACKING_PROPERTY, ignoreUnknownFields = false)
public record LinkTrackingSchedulerProperties(
        @NotNull Duration periodMinutes,
        @NotNull Duration retryDelayMinutes,
        @NotNull Integer linksLimitPerTrack,
        @NotNull Integer threadsCount,
        @NotNull Integer awaitTimeoutMinutes) {}
