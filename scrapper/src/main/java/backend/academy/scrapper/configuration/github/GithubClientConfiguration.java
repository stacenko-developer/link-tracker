package backend.academy.scrapper.configuration.github;

import backend.academy.scrapper.client.github.GithubClient;
import backend.academy.scrapper.client.github.GithubClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GithubClientProperties.class)
public class GithubClientConfiguration {

    @Bean
    public GithubClient githubClient(GithubClientProperties githubClientProperties) {
        return GithubClientFactory.create(
                githubClientProperties.baseUrl(),
                githubClientProperties.connectTimeoutMillis(),
                githubClientProperties.readTimeoutMillis(),
                githubClientProperties.responseTimeoutMillis());
    }
}
