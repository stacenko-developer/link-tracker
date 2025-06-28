package backend.academy.bot.client.scrapper;

import static backend.academy.bot.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.bot.ConstValues.DEFAULT_SCENARIO_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

import backend.academy.bot.TestConfiguration;
import backend.academy.bot.configuration.BotConfiguration;
import backend.academy.common.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

@WireMockTest(httpPort = 8090)
public abstract class CommonClientServiceTest extends TestConfiguration {

    private static final String ALL_KEYS_PATTERN = "*";

    protected static final int DEFAULT_RATION_FOR_FIXED_DELAY = 2;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected BotConfiguration botConfiguration;

    @Autowired
    protected RetryRegistry retryRegistry;

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    protected void setUp() {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(ALL_KEYS_PATTERN)));
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::transitionToClosedState);
    }

    @SneakyThrows
    protected <T> void executeRetryTest(
            RequestPatternBuilder requestPatternBuilder,
            MappingBuilder mappingBuilder,
            int httpStatusCode,
            boolean shouldRetry,
            T expectedResult,
            Supplier<ResponseDto<T>> request) {
        String retryName =
                botConfiguration.resilienceInstances().scrapperService().retry();
        int maxAttempts = getMaxAttempts(retryName);

        setupRetryScenario(expectedResult, mappingBuilder, httpStatusCode, maxAttempts);

        ResponseDto<T> expectedResponseDto = shouldRetry
                ? new ResponseDto<>(expectedResult, null)
                : new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        int expectedCallsCount = shouldRetry ? maxAttempts : 1;

        ResponseDto<T> actualResponse = request.get();

        Assertions.assertEquals(expectedResponseDto, actualResponse);

        WireMock.verify(expectedCallsCount, requestPatternBuilder);
    }

    protected <T> void verifyCircuitBreakerRejectsCall(Supplier<ResponseDto<T>> request, int fixedDelay) {
        String circuitBreakerName =
                botConfiguration.resilienceInstances().scrapperService().circuitBreaker();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToOpenState();

        String expectedExceptionName = CallNotPermittedException.class.getSimpleName();
        int expectedNumberOfNotPermittedCalls = 1;
        int expectedNumberOfSuccessFulCalls = 0;

        long startTime = System.currentTimeMillis();
        ResponseDto<T> actualResponse = request.get();
        long executionTime = System.currentTimeMillis() - startTime;

        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        Assertions.assertTrue(executionTime < fixedDelay);
        Assertions.assertNull(actualResponse.content());
        Assertions.assertEquals(
                expectedExceptionName, actualResponse.apiErrorResponse().exceptionName());

        Assertions.assertEquals(expectedNumberOfNotPermittedCalls, metrics.getNumberOfNotPermittedCalls());
        Assertions.assertEquals(expectedNumberOfSuccessFulCalls, metrics.getNumberOfSuccessfulCalls());
    }

    @SneakyThrows
    protected <T> void setupRetryScenario(
            T expectedResult, MappingBuilder mappingBuilder, int httpStatusCode, int retryCount) {
        String lastState = String.valueOf(retryCount);

        for (int i = 1; i < retryCount; i++) {
            String currentState = i == 1 ? STARTED : String.valueOf(i);
            String nextState = String.valueOf(i + 1);

            stubFor(mappingBuilder
                    .withId(UUID.randomUUID())
                    .inScenario(DEFAULT_SCENARIO_NAME)
                    .whenScenarioStateIs(currentState)
                    .willReturn(aResponse()
                            .withStatus(httpStatusCode)
                            .withBody(objectMapper.writeValueAsString(DEFAULT_API_ERROR_RESPONSE)))
                    .willSetStateTo(nextState));
        }

        stubFor(mappingBuilder
                .withId(UUID.randomUUID())
                .inScenario(DEFAULT_SCENARIO_NAME)
                .whenScenarioStateIs(lastState)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(expectedResult))));
    }

    @SneakyThrows
    protected ResponseDefinitionBuilder getBadRequestResponse() {
        return aResponse()
                .withStatus(HttpStatus.BAD_REQUEST_400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(DEFAULT_API_ERROR_RESPONSE));
    }

    protected static List<Integer> getServerErrorCodes() {
        return List.of(500, 501, 502, 503, 504);
    }

    protected static List<Integer> getClientErrorCodes() {
        return List.of(400, 401, 402, 403, 404);
    }

    private int getMaxAttempts(String retryName) {
        Retry retry = retryRegistry.retry(retryName);
        RetryConfig config = retry.getRetryConfig();

        return config.getMaxAttempts();
    }
}
