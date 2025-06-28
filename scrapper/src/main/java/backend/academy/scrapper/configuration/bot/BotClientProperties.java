package backend.academy.scrapper.configuration.bot;

import static backend.academy.scrapper.constants.ConfigurationConstants.BOT_CLIENT_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = BOT_CLIENT_PROPERTY, ignoreUnknownFields = false)
public record BotClientProperties(
        @NotBlank String baseUrl,
        @NotNull Integer connectTimeoutMillis,
        @NotNull Integer readTimeoutMillis,
        @NotNull Long responseTimeoutMillis) {}
