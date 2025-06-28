package backend.academy.scrapper.configuration.stackoverflow;

import static backend.academy.scrapper.constants.ConfigurationConstants.STACKOVERFLOW_CLIENT_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = STACKOVERFLOW_CLIENT_PROPERTY, ignoreUnknownFields = false)
public record StackoverflowClientProperties(
        @NotBlank String baseUrl,
        @NotNull Integer connectTimeoutMillis,
        @NotNull Integer readTimeoutMillis,
        @NotNull Long responseTimeoutMillis) {}
