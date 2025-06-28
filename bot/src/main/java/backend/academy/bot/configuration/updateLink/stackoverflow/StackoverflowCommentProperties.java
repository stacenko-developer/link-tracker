package backend.academy.bot.configuration.updateLink.stackoverflow;

import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;
import static backend.academy.bot.constants.ConfigurationConstants.STACKOVERFLOW_COMMENT_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = STACKOVERFLOW_COMMENT_PROPERTY, ignoreUnknownFields = false)
public record StackoverflowCommentProperties(@NotBlank String name, @NotBlank String message) {}
