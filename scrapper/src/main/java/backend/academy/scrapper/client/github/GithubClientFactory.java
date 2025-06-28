package backend.academy.scrapper.client.github;

import backend.academy.common.factory.ClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubClientFactory {

    public static GithubClient create(
            String baseUrl, Integer connectTimeoutMillis, Integer readTimeoutMillis, Long responseTimeoutMillis) {
        return ClientFactory.create(
                GithubClient.class, baseUrl, connectTimeoutMillis, readTimeoutMillis, responseTimeoutMillis);
    }
}
