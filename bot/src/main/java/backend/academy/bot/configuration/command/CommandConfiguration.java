package backend.academy.bot.configuration.command;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    StartCommandProperties.class,
    HelpCommandProperties.class,
    ListCommandProperties.class,
    TrackCommandProperties.class,
    UntrackCommandProperties.class,
    NotificationModeProperties.class,
    ErrorMessageProperties.class
})
public class CommandConfiguration {}
