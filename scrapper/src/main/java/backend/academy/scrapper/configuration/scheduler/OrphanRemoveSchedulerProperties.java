package backend.academy.scrapper.configuration.scheduler;

import static backend.academy.scrapper.constants.ConfigurationConstants.ORPHAN_REMOVE_PROPERTY;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = ORPHAN_REMOVE_PROPERTY, ignoreUnknownFields = false)
public record OrphanRemoveSchedulerProperties(@NotNull Duration periodMinutes) {}
