package backend.academy.bot.configuration.resilience;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RateLimiterProperties.class})
public class ResilienceConfiguration {}
