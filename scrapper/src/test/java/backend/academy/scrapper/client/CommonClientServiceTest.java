package backend.academy.scrapper.client;

import static backend.academy.scrapper.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.scrapper.ConstValues.DEFAULT_DIGEST_LINK_UPDATE;
import static backend.academy.scrapper.ConstValues.DEFAULT_IMMEDIATE_LINK_UPDATE;
import static backend.academy.scrapper.ConstValues.DEFAULT_SCENARIO_NAME;
import static backend.academy.scrapper.ConstValues.DIGEST_UPDATE_URL;
import static backend.academy.scrapper.ConstValues.IMMEDIATE_UPDATE_URL;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.configuration.bot.BotClientProperties;
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
import java.util.UUID;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

@WireMockTest(httpPort = 8090)
public abstract class CommonClientServiceTest extends TestConfiguration {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ScrapperConfig scrapperConfig;

    @Autowired
    protected RetryRegistry retryRegistry;

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    protected BotClientProperties botClientProperties;

    @BeforeEach
    protected void setUp() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(CircuitBreaker::transitionToClosedState);
    }

    @SneakyThrows
    protected <T> void executeRetryTest(
            RequestPatternBuilder requestPatternBuilder,
            MappingBuilder mappingBuilder,
            int httpStatusCode,
            boolean shouldRetry,
            T expectedResult,
            Supplier<ResponseDto<T>> request,
            String retryName) {
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

    @SneakyThrows
    protected void executeRetryTest(
            RequestPatternBuilder requestPatternBuilder,
            MappingBuilder mappingBuilder,
            int httpStatusCode,
            boolean shouldRetry,
            Runnable runnable,
            String retryName) {
        int maxAttempts = getMaxAttempts(retryName);

        setupRetryScenario(null, mappingBuilder, httpStatusCode, maxAttempts);

        int expectedCallsCount = shouldRetry ? maxAttempts : 1;

        runnable.run();

        WireMock.verify(expectedCallsCount, requestPatternBuilder);
    }

    protected <T> void verifyCircuitBreakerRejectsCall(
            Supplier<ResponseDto<T>> request, int fixedDelay, String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToOpenState();

        String expectedExceptionName = CallNotPermittedException.class.getSimpleName();

        long startTime = System.currentTimeMillis();
        ResponseDto<T> actualResponse = request.get();
        long executionTime = System.currentTimeMillis() - startTime;

        Assertions.assertTrue(executionTime < fixedDelay);
        Assertions.assertNull(actualResponse.content());
        Assertions.assertEquals(
                expectedExceptionName, actualResponse.apiErrorResponse().exceptionName());

        checkCircuitBreakerMetricsWhenCircuitBreakerIsOpen(circuitBreaker);
    }

    protected void verifyCircuitBreakerRejectsCall(Runnable runnable, int fixedDelay, String circuitBreakerName) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        circuitBreaker.transitionToOpenState();

        long startTime = System.currentTimeMillis();
        runnable.run();
        long executionTime = System.currentTimeMillis() - startTime;

        Assertions.assertTrue(executionTime < fixedDelay);

        checkCircuitBreakerMetricsWhenCircuitBreakerIsOpen(circuitBreaker);
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

    @SneakyThrows
    protected void setUpWiremockImmediateUpdateToReturnDefaultCorrectResponse() {
        stubFor(immediateUpdateMappingBuilder()
                .willReturn(aResponse().withStatus(HttpStatus.OK_200).withFixedDelay(getFixedDelayForBotClient())));
    }

    @SneakyThrows
    protected void setUpWiremockDigestUpdateToReturnDefaultCorrectResponse() {
        stubFor(digestUpdateMappingBuilder()
                .willReturn(aResponse().withStatus(HttpStatus.OK_200).withFixedDelay(getFixedDelayForBotClient())));
    }

    @SneakyThrows
    protected void setUpWiremockImmediateUpdateToReturnServerError() {
        stubFor(immediateUpdateMappingBuilder().willReturn(serverError()));
    }

    @SneakyThrows
    protected void setUpWiremockDigestUpdateToReturnServerError() {
        stubFor(digestUpdateMappingBuilder().willReturn(serverError()));
    }

    @SneakyThrows
    protected MappingBuilder immediateUpdateMappingBuilder() {
        return post(urlPathMatching(IMMEDIATE_UPDATE_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_IMMEDIATE_LINK_UPDATE)));
    }

    @SneakyThrows
    protected MappingBuilder digestUpdateMappingBuilder() {
        return post(urlPathMatching(DIGEST_UPDATE_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_DIGEST_LINK_UPDATE)));
    }

    @SneakyThrows
    protected RequestPatternBuilder immediateUpdateRequestPatternBuilder() {
        return postRequestedFor(urlPathMatching(IMMEDIATE_UPDATE_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_IMMEDIATE_LINK_UPDATE)));
    }

    @SneakyThrows
    protected RequestPatternBuilder digestUpdateRequestPatternBuilder() {
        return postRequestedFor(urlPathMatching(DIGEST_UPDATE_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_DIGEST_LINK_UPDATE)));
    }

    protected int getFixedDelayForBotClient() {
        return (int) (botClientProperties.responseTimeoutMillis() / 2);
    }

    private int getMaxAttempts(String retryName) {
        Retry retry = retryRegistry.retry(retryName);
        RetryConfig config = retry.getRetryConfig();

        return config.getMaxAttempts();
    }

    private void checkCircuitBreakerMetricsWhenCircuitBreakerIsOpen(CircuitBreaker circuitBreaker) {
        int expectedNumberOfNotPermittedCalls = 1;
        int expectedNumberOfSuccessFulCalls = 0;

        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        Assertions.assertEquals(expectedNumberOfNotPermittedCalls, metrics.getNumberOfNotPermittedCalls());
        Assertions.assertEquals(expectedNumberOfSuccessFulCalls, metrics.getNumberOfSuccessfulCalls());
    }
}
