package backend.academy.bot.configuration.client.scrapper;

import backend.academy.bot.client.scrapper.ScrapperClient;
import backend.academy.bot.client.scrapper.ScrapperClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ScrapperClientProperties.class)
public class ScrapperClientConfiguration {

    @Bean
    public ScrapperClient scrapperClient(ScrapperClientProperties scrapperClientProperties) {
        return ScrapperClientFactory.create(
                scrapperClientProperties.baseUrl(),
                scrapperClientProperties.connectTimeoutMillis(),
                scrapperClientProperties.readTimeoutMillis(),
                scrapperClientProperties.responseTimeoutMillis());
    }
}
