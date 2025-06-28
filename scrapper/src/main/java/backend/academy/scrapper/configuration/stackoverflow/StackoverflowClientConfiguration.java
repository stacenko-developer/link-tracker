package backend.academy.scrapper.configuration.stackoverflow;

import backend.academy.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.scrapper.client.stackoverflow.StackoverflowClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StackoverflowClientProperties.class)
public class StackoverflowClientConfiguration {

    @Bean
    public StackoverflowClient stackoverflowClient(StackoverflowClientProperties stackoverflowClientProperties) {
        return StackoverflowClientFactory.create(
                stackoverflowClientProperties.baseUrl(),
                stackoverflowClientProperties.connectTimeoutMillis(),
                stackoverflowClientProperties.readTimeoutMillis(),
                stackoverflowClientProperties.responseTimeoutMillis());
    }
}
