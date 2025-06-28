package backend.academy.scrapper.client.stackoverflow;

import backend.academy.common.factory.ClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackoverflowClientFactory {

    public static StackoverflowClient create(
            String baseUrl, Integer connectTimeoutMillis, Integer readTimeoutMillis, Long responseTimeoutMillis) {
        return ClientFactory.create(
                StackoverflowClient.class, baseUrl, connectTimeoutMillis, readTimeoutMillis, responseTimeoutMillis);
    }
}
