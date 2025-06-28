package backend.academy.scrapper.service.client;

import static backend.academy.scrapper.constants.ResilienceConstValues.DEFAULT_FALLBACK_METHOD_NAME;
import static backend.academy.scrapper.constants.ResilienceConstValues.GITHUB_CIRCUIT_BREAKER;
import static backend.academy.scrapper.constants.ResilienceConstValues.GITHUB_RETRY;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.github.GithubClient;
import backend.academy.scrapper.client.github.dto.GithubEventDto;
import backend.academy.scrapper.configuration.ScrapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GithubClientService extends CommonClientService {

    private static final String FALLBACK_EXCEPTION_MESSAGE = "exception in fallback method in github client: ";

    private final GithubClient githubClient;
    private final ScrapperConfig scrapperConfig;

    public GithubClientService(ObjectMapper objectMapper, GithubClient githubClient, ScrapperConfig scrapperConfig) {
        super(objectMapper);
        this.githubClient = githubClient;
        this.scrapperConfig = scrapperConfig;
    }

    @Retry(name = GITHUB_RETRY)
    @CircuitBreaker(name = GITHUB_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<List<GithubEventDto>> getRepositoryEvents(String owner, String repository) {
        return execute(() -> githubClient.getRepositoryEvents(scrapperConfig.githubToken(), owner, repository));
    }

    public ResponseDto<List<GithubEventDto>> fallback(Throwable t) {
        log.error(FALLBACK_EXCEPTION_MESSAGE, t);

        return new ResponseDto<>(null, getApiErrorResponse(t));
    }
}
