package backend.academy.bot.service.client;

import static backend.academy.bot.constants.CacheConstValues.BOT_TRACKING_LINKS_CACHE_NAME;
import static backend.academy.bot.constants.ResilienceConstValues.SCRAPPER_SERVICE_CIRCUIT_BREAKER;
import static backend.academy.bot.constants.ResilienceConstValues.SCRAPPER_SERVICE_RETRY;

import backend.academy.bot.cache.cleaner.CacheCleaner;
import backend.academy.bot.client.scrapper.ScrapperClient;
import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import backend.academy.bot.client.scrapper.dto.request.AddLinkRequest;
import backend.academy.bot.client.scrapper.dto.request.ChatSettingsRequest;
import backend.academy.bot.client.scrapper.dto.request.FindUserLinksRequest;
import backend.academy.bot.client.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.bot.client.scrapper.dto.response.ChatResponse;
import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import backend.academy.common.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScrapperClientService extends CommonClientService {

    private static final String FALLBACK_MESSAGE = "exception in fallback method: ";
    private static final String DEFAULT_FALLBACK_METHOD_NAME = "fallback";

    private final ScrapperClient scrapperClient;

    public ScrapperClientService(ObjectMapper objectMapper, CacheCleaner cacheCleaner, ScrapperClient scrapperClient) {
        super(objectMapper, cacheCleaner);
        this.scrapperClient = scrapperClient;
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    @Cacheable(
            value = BOT_TRACKING_LINKS_CACHE_NAME,
            key = "T(backend.academy.bot.cache.CacheKeyGenerator).generateKey(#tgChatId, #tagNames)",
            unless = "#result.apiErrorResponse() != null")
    public ResponseDto<ListLinksResponse> getAllUserTrackingLinks(Long tgChatId, List<String> tagNames) {
        return execute(() -> scrapperClient.getAllUserTrackingLinks(new FindUserLinksRequest(tgChatId, tagNames)));
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return processWithCacheCleanup(
                () -> scrapperClient.addLink(tgChatId, addLinkRequest), tgChatId, addLinkRequest.tags());
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<LinkResponse> deleteLink(Long tgChatId, URI url) {
        ResponseDto<LinkResponse> result =
                execute(() -> scrapperClient.deleteLink(tgChatId, new RemoveLinkRequest(url)));

        List<String> tags = result.content() == null ? null : result.content().tags();

        cleanCacheProcess(result.apiErrorResponse(), tgChatId, tags);

        return result;
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<Void> registerChat(Long tgChatId) {
        return processWithCacheCleanup(() -> scrapperClient.registerChat(tgChatId), tgChatId, null);
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<ChatResponse> updateChatSettings(Long chatId, ChatSettingsRequest chatSettingsRequest) {
        return execute(() -> scrapperClient.updateChatSettings(chatId, chatSettingsRequest));
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<ChatResponse> getChatById(Long chatId) {
        return execute(() -> scrapperClient.getChatById(chatId));
    }

    @Retry(name = SCRAPPER_SERVICE_RETRY)
    @CircuitBreaker(name = SCRAPPER_SERVICE_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<List<NotificationModeDto>> getNotificationModes() {
        return execute(scrapperClient::getNotificationModes);
    }

    public <T> ResponseDto<T> fallback(Throwable t) {
        log.error(FALLBACK_MESSAGE, t);
        return new ResponseDto<>(null, getApiErrorResponse(t));
    }
}
