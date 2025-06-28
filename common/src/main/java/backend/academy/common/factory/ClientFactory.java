package backend.academy.common.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
@RequiredArgsConstructor
public class ClientFactory {

    private static final HttpServiceProxyFactory.Builder FACTORY_BUILDER = HttpServiceProxyFactory.builder();

    public static <T> T create(
            Class<T> clientClass,
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer readTimeoutMillis,
            Long responseTimeoutMillis) {
        WebClient webClient =
                WebClientFactory.create(baseUrl, connectTimeoutMillis, readTimeoutMillis, responseTimeoutMillis);

        return FACTORY_BUILDER
                .exchangeAdapter(WebClientAdapter.create(webClient))
                .build()
                .createClient(clientClass);
    }
}
