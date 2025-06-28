package backend.academy.scrapper.configuration.bot;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.client.bot.BotClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BotClientProperties.class)
public class BotClientConfiguration {

    @Bean
    public BotClient botClient(BotClientProperties botClientProperties) {
        return BotClientFactory.create(
                botClientProperties.baseUrl(),
                botClientProperties.connectTimeoutMillis(),
                botClientProperties.readTimeoutMillis(),
                botClientProperties.responseTimeoutMillis());
    }
}
