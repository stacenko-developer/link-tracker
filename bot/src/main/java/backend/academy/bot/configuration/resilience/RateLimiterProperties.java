package backend.academy.bot.configuration.resilience;

import static backend.academy.bot.constants.ConfigurationConstants.RATE_LIMITER_PROPERTY;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = RATE_LIMITER_PROPERTY)
public record RateLimiterProperties(
        @NotNull Integer limitForPeriod, @NotNull Duration limitRefreshPeriod, @NotNull Duration timeoutDuration) {}
