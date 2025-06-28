package backend.academy.scrapper.client;

import static backend.academy.scrapper.ConstValues.AUTHORIZATION_HEADER_NAME;
import static backend.academy.scrapper.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.scrapper.ConstValues.DEFAULT_EVENTS;
import static backend.academy.scrapper.ConstValues.DEFAULT_OWNER;
import static backend.academy.scrapper.ConstValues.DEFAULT_REPO;
import static backend.academy.scrapper.ConstValues.GET_EVENTS_URL;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.github.dto.GithubEventDto;
import backend.academy.scrapper.configuration.github.GithubClientProperties;
import backend.academy.scrapper.service.client.GithubClientService;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

public class GithubClientServiceTest extends CommonClientServiceTest {

    @Autowired
    private GithubClientService githubServiceClient;

    @Autowired
    private GithubClientProperties githubClientProperties;

    @Test
    public void getRepositoryEventsWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<List<GithubEventDto>> expectedResponse = new ResponseDto<>(DEFAULT_EVENTS, null);

        setUpWiremockGetRepositoryEventsToReturnDefaultCorrectResponse();

        ResponseDto<List<GithubEventDto>> actualResponse =
                githubServiceClient.getRepositoryEvents(DEFAULT_OWNER, DEFAULT_REPO);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void getRepositoryEventsWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(getRepositoryEventsMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<List<GithubEventDto>> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<List<GithubEventDto>> actualResponse =
                githubServiceClient.getRepositoryEvents(DEFAULT_OWNER, DEFAULT_REPO);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void getRepositoryEventsWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetRepositoryEventsToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> githubServiceClient.getRepositoryEvents(DEFAULT_OWNER, DEFAULT_REPO),
                getFixedDelayForGithubClient(),
                scrapperConfig.resilienceInstances().github().circuitBreaker());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getAllUserTrackingLinksWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetRepositoryEventsRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getAllUserTrackingLinksWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetRepositoryEventsRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    private void executeGetRepositoryEventsRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getRepositoryEventsRequestPatternBuilder(),
                getRepositoryEventsMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_EVENTS,
                () -> githubServiceClient.getRepositoryEvents(DEFAULT_OWNER, DEFAULT_REPO),
                scrapperConfig.resilienceInstances().github().retry());
    }

    @SneakyThrows
    private void setUpWiremockGetRepositoryEventsToReturnDefaultCorrectResponse() {
        stubFor(getRepositoryEventsMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_EVENTS))
                        .withFixedDelay(getFixedDelayForGithubClient())));
    }

    @SneakyThrows
    private MappingBuilder getRepositoryEventsMappingBuilder() {
        return get(urlPathMatching(GET_EVENTS_URL))
                .withHeader(AUTHORIZATION_HEADER_NAME, equalTo(scrapperConfig.githubToken()));
    }

    @SneakyThrows
    private RequestPatternBuilder getRepositoryEventsRequestPatternBuilder() {
        return getRequestedFor(urlPathMatching(GET_EVENTS_URL))
                .withHeader(AUTHORIZATION_HEADER_NAME, equalTo(scrapperConfig.githubToken()));
    }

    private int getFixedDelayForGithubClient() {
        return (int) (githubClientProperties.responseTimeoutMillis() / 2);
    }
}
