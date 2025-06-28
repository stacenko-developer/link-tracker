package backend.academy.scrapper.service.client;

import static backend.academy.scrapper.constants.ResilienceConstValues.DEFAULT_FALLBACK_METHOD_NAME;
import static backend.academy.scrapper.constants.ResilienceConstValues.STACKOVERFLOW_CIRCUIT_BREAKER;
import static backend.academy.scrapper.constants.ResilienceConstValues.STACKOVERFLOW_RETRY;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.stackoverflow.StackoverflowClient;
import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import backend.academy.scrapper.configuration.ScrapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StackoverflowClientService extends CommonClientService {

    private static final String FALLBACK_EXCEPTION_MESSAGE = "exception in fallback method in stackoverflow client: ";

    private final StackoverflowClient stackoverflowClient;
    private final ScrapperConfig scrapperConfig;

    public StackoverflowClientService(
            ObjectMapper objectMapper, StackoverflowClient stackoverflowClient, ScrapperConfig scrapperConfig) {
        super(objectMapper);
        this.stackoverflowClient = stackoverflowClient;
        this.scrapperConfig = scrapperConfig;
    }

    @Retry(name = STACKOVERFLOW_RETRY)
    @CircuitBreaker(name = STACKOVERFLOW_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<StackoverflowResponseDto> getAnswers(Long questionId) {
        return execute(() -> stackoverflowClient.getAnswers(
                questionId,
                scrapperConfig.stackOverflow().key(),
                scrapperConfig.stackOverflow().accessToken()));
    }

    @Retry(name = STACKOVERFLOW_RETRY)
    @CircuitBreaker(name = STACKOVERFLOW_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<StackoverflowResponseDto> getComments(Long questionId) {
        return execute(() -> stackoverflowClient.getComments(
                questionId,
                scrapperConfig.stackOverflow().key(),
                scrapperConfig.stackOverflow().accessToken()));
    }

    @Retry(name = STACKOVERFLOW_RETRY)
    @CircuitBreaker(name = STACKOVERFLOW_CIRCUIT_BREAKER, fallbackMethod = DEFAULT_FALLBACK_METHOD_NAME)
    public ResponseDto<StackoverflowResponseDto> getQuestionInformation(Long questionId) {
        return execute(() -> stackoverflowClient.getQuestionInformation(
                questionId,
                scrapperConfig.stackOverflow().key(),
                scrapperConfig.stackOverflow().accessToken()));
    }

    public ResponseDto<StackoverflowResponseDto> fallback(Throwable t) {
        log.error(FALLBACK_EXCEPTION_MESSAGE, t);

        return new ResponseDto<>(null, getApiErrorResponse(t));
    }
}
