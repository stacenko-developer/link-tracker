package backend.academy.scrapper.configuration.github;

import static backend.academy.scrapper.constants.ConfigurationConstants.GITHUB_CLIENT_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = GITHUB_CLIENT_PROPERTY, ignoreUnknownFields = false)
public record GithubClientProperties(
        @NotBlank String baseUrl,
        @NotNull Integer connectTimeoutMillis,
        @NotNull Integer readTimeoutMillis,
        @NotNull Long responseTimeoutMillis) {}
