package backend.academy.bot.configuration.updateLink.stackoverflow;

import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;
import static backend.academy.bot.constants.ConfigurationConstants.STACKOVERFLOW_ANSWER_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = STACKOVERFLOW_ANSWER_PROPERTY, ignoreUnknownFields = false)
public record StackoverflowAnswerProperties(@NotBlank String name, @NotBlank String message) {}
