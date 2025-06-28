package backend.academy.scrapper.client.bot;

import backend.academy.common.factory.ClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotClientFactory {

    public static BotClient create(
            String baseUrl, Integer connectTimeoutMillis, Integer readTimeoutMillis, Long responseTimeoutMillis) {
        return ClientFactory.create(
                BotClient.class, baseUrl, connectTimeoutMillis, readTimeoutMillis, responseTimeoutMillis);
    }
}
