package backend.academy.bot.configuration.updateLink;

import static backend.academy.bot.constants.ConfigurationConstants.DIGEST_PROPERTY;
import static backend.academy.bot.constants.ConfigurationConstants.MESSAGE_FILE;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Validated
@PropertySource(value = MESSAGE_FILE)
@ConfigurationProperties(prefix = DIGEST_PROPERTY, ignoreUnknownFields = false)
public record DigestProperties(@NotBlank String header) {}
