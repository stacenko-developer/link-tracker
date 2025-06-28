package backend.academy.bot.configuration.client.scrapper;

import static backend.academy.bot.constants.ConfigurationConstants.SCRAPPER_CLIENT_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = SCRAPPER_CLIENT_PROPERTY, ignoreUnknownFields = false)
public record ScrapperClientProperties(
        @NotBlank String baseUrl,
        @NotNull Integer connectTimeoutMillis,
        @NotNull Integer readTimeoutMillis,
        @NotNull Long responseTimeoutMillis) {}
