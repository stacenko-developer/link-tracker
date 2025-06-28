package backend.academy.bot.configuration.command;

import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;
import static backend.academy.bot.constants.ConfigurationConstants.UNTRACK_COMMAND_PROPERTY;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = UNTRACK_COMMAND_PROPERTY, ignoreUnknownFields = false)
public record UntrackCommandProperties(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String usageInformation,
        @NotBlank String success,
        @NotBlank String unregisteredAccount,
        @NotBlank String incorrectCommandFormat,
        @NotBlank String notFoundLink) {}
