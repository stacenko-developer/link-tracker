package backend.academy.bot.client.scrapper;

import backend.academy.common.factory.ClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperClientFactory {

    public static ScrapperClient create(
            String baseUrl, Integer connectTimeoutMillis, Integer readTimeoutMillis, Long responseTimeoutMillis) {
        return ClientFactory.create(
                ScrapperClient.class, baseUrl, connectTimeoutMillis, readTimeoutMillis, responseTimeoutMillis);
    }
}
