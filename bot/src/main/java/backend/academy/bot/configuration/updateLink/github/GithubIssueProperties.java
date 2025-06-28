package backend.academy.bot.configuration.updateLink.github;

import static backend.academy.bot.constants.ConfigurationConstants.GITHUB_ISSUE_PROPERTY;
import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = GITHUB_ISSUE_PROPERTY, ignoreUnknownFields = false)
public record GithubIssueProperties(@NotBlank String name, @NotBlank String message) {}
