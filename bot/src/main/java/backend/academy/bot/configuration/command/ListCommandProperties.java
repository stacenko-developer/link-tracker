package backend.academy.bot.configuration.command;

import static backend.academy.bot.constants.ConfigurationConstants.LIST_COMMAND_PROPERTY;
import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = LIST_COMMAND_PROPERTY, ignoreUnknownFields = false)
public record ListCommandProperties(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String usageInformation,
        @NotBlank String header,
        @NotBlank String format,
        @NotBlank String trackingLinksNotFound,
        @NotBlank String linksByTagsNotFound,
        @NotBlank String unregisteredAccount) {}
