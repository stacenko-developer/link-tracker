package backend.academy.scrapper.configuration.scheduler;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    OrphanRemoveSchedulerProperties.class,
    LinkTrackingSchedulerProperties.class,
    SendDigestNotificationsProperties.class
})
public class SchedulerConfiguration {}
