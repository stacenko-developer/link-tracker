package backend.academy.bot.configuration.command;

import static backend.academy.bot.constants.ConfigurationConstants.ERROR_MESSAGE_PROPERTY;
import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = ERROR_MESSAGE_PROPERTY, ignoreUnknownFields = false)
public record ErrorMessageProperties(@NotBlank String serverError, @NotBlank String unknownCommand) {}
