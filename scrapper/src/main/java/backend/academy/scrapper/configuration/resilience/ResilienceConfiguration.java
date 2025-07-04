package backend.academy.scrapper.configuration.resilience;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RateLimiterProperties.class})
public class ResilienceConfiguration {}
