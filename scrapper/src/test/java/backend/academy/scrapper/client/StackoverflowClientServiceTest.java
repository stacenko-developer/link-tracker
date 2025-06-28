package backend.academy.scrapper.client;

import static backend.academy.scrapper.ConstValues.ACCESS_TOKEN_QUERY_PARAM_NAME;
import static backend.academy.scrapper.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.scrapper.ConstValues.DEFAULT_QUESTION_ID;
import static backend.academy.scrapper.ConstValues.DEFAULT_STACKOVERFLOW_RESPONSE_DTO;
import static backend.academy.scrapper.ConstValues.FILTER_QUERY_PARAM_NAME;
import static backend.academy.scrapper.ConstValues.FILTER_QUERY_PARAM_VALUE;
import static backend.academy.scrapper.ConstValues.GET_ANSWERS_URL_FORMAT;
import static backend.academy.scrapper.ConstValues.GET_COMMENTS_URL_FORMAT;
import static backend.academy.scrapper.ConstValues.GET_QUESTION_INFORMATION_URL_FORMAT;
import static backend.academy.scrapper.ConstValues.KEY_QUERY_PARAM_NAME;
import static backend.academy.scrapper.ConstValues.SITE_QUERY_PARAM_NAME;
import static backend.academy.scrapper.ConstValues.SITE_QUERY_PARAM_VALUE;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.client.stackoverflow.dto.StackoverflowResponseDto;
import backend.academy.scrapper.configuration.stackoverflow.StackoverflowClientProperties;
import backend.academy.scrapper.service.client.StackoverflowClientService;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

public class StackoverflowClientServiceTest extends CommonClientServiceTest {

    @Autowired
    private StackoverflowClientService stackoverflowServiceClient;

    @Autowired
    private StackoverflowClientProperties stackoverflowClientProperties;

    @Test
    public void getAnswersWithCorrectResponse_ShouldReturnAnswers() {
        ResponseDto<StackoverflowResponseDto> expectedResponse =
                new ResponseDto<>(DEFAULT_STACKOVERFLOW_RESPONSE_DTO, null);

        setUpWiremockGetAnswersToReturnDefaultCorrectResponse();

        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getAnswers(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getCommentsWithCorrectResponse_ShouldReturnComments() {
        ResponseDto<StackoverflowResponseDto> expectedResponse =
                new ResponseDto<>(DEFAULT_STACKOVERFLOW_RESPONSE_DTO, null);

        setUpWiremockGetCommentsToReturnDefaultCorrectResponse();

        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getComments(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getQuestionInformationWithCorrectResponse_ShouldReturnQuestionInformation() {
        ResponseDto<StackoverflowResponseDto> expectedResponse =
                new ResponseDto<>(DEFAULT_STACKOVERFLOW_RESPONSE_DTO, null);

        setUpWiremockGetQuestionInformationToReturnDefaultCorrectResponse();

        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getQuestionInformation(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getAnswersWithWebClientResponseException_ShouldReturnNull() {
        stubFor(getAnswersMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<StackoverflowResponseDto> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getAnswers(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getCommentsWithWebClientResponseException_ShouldReturnNull() {
        stubFor(getCommentsMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<StackoverflowResponseDto> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getComments(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void getQuestionInformationWithWebClientResponseException_ShouldReturnNull() {
        stubFor(getQuestionInformationMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<StackoverflowResponseDto> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<StackoverflowResponseDto> actualResponse =
                stackoverflowServiceClient.getQuestionInformation(DEFAULT_QUESTION_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @SneakyThrows
    public void getAnswersWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetAnswersToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> stackoverflowServiceClient.getAnswers(DEFAULT_QUESTION_ID),
                getFixedDelayForStackoverflowClient(),
                scrapperConfig.resilienceInstances().stackoverflow().circuitBreaker());
    }

    @Test
    @SneakyThrows
    public void getCommentsWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetCommentsToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> stackoverflowServiceClient.getComments(DEFAULT_QUESTION_ID),
                getFixedDelayForStackoverflowClient(),
                scrapperConfig.resilienceInstances().stackoverflow().circuitBreaker());
    }

    @Test
    @SneakyThrows
    public void getQuestionInformationWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetQuestionInformationToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> stackoverflowServiceClient.getQuestionInformation(DEFAULT_QUESTION_ID),
                getFixedDelayForStackoverflowClient(),
                scrapperConfig.resilienceInstances().stackoverflow().circuitBreaker());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getAnswersWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetAnswersRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getAnswersWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetAnswersRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getCommentsWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetCommentsRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getCommentsWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetCommentsRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getQuestionInformationWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetQuestionInformationRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getQuestionInformationWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetQuestionInformationRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    private void executeGetAnswersRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getAnswersRequestPatternBuilder(),
                getAnswersMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_STACKOVERFLOW_RESPONSE_DTO,
                () -> stackoverflowServiceClient.getAnswers(DEFAULT_QUESTION_ID),
                scrapperConfig.resilienceInstances().stackoverflow().retry());
    }

    @SneakyThrows
    private void executeGetCommentsRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getCommentsRequestPatternBuilder(),
                getCommentsMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_STACKOVERFLOW_RESPONSE_DTO,
                () -> stackoverflowServiceClient.getComments(DEFAULT_QUESTION_ID),
                scrapperConfig.resilienceInstances().stackoverflow().retry());
    }

    @SneakyThrows
    private void executeGetQuestionInformationRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getQuestionInformationRequestPatternBuilder(),
                getQuestionInformationMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_STACKOVERFLOW_RESPONSE_DTO,
                () -> stackoverflowServiceClient.getQuestionInformation(DEFAULT_QUESTION_ID),
                scrapperConfig.resilienceInstances().stackoverflow().retry());
    }

    @SneakyThrows
    private void setUpWiremockGetAnswersToReturnDefaultCorrectResponse() {
        stubFor(getAnswersMappingBuilder().willReturn(getDefaultCorrectResponse()));
    }

    @SneakyThrows
    private void setUpWiremockGetCommentsToReturnDefaultCorrectResponse() {
        stubFor(getCommentsMappingBuilder().willReturn(getDefaultCorrectResponse()));
    }

    @SneakyThrows
    private void setUpWiremockGetQuestionInformationToReturnDefaultCorrectResponse() {
        stubFor(getQuestionInformationMappingBuilder().willReturn(getDefaultCorrectResponse()));
    }

    @SneakyThrows
    private MappingBuilder getAnswersMappingBuilder() {
        MappingBuilder mappingBuilder =
                get(urlPathMatching(String.format(GET_ANSWERS_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(mappingBuilder);
        return mappingBuilder;
    }

    @SneakyThrows
    private MappingBuilder getCommentsMappingBuilder() {
        MappingBuilder mappingBuilder =
                get(urlPathMatching(String.format(GET_COMMENTS_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(mappingBuilder);
        return mappingBuilder;
    }

    @SneakyThrows
    private MappingBuilder getQuestionInformationMappingBuilder() {
        MappingBuilder mappingBuilder =
                get(urlPathMatching(String.format(GET_QUESTION_INFORMATION_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(mappingBuilder);
        return mappingBuilder;
    }

    @SneakyThrows
    private RequestPatternBuilder getAnswersRequestPatternBuilder() {
        RequestPatternBuilder requestPatternBuilder =
                getRequestedFor(urlPathMatching(String.format(GET_ANSWERS_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(requestPatternBuilder);
        return requestPatternBuilder;
    }

    @SneakyThrows
    private RequestPatternBuilder getCommentsRequestPatternBuilder() {
        RequestPatternBuilder requestPatternBuilder =
                getRequestedFor(urlPathMatching(String.format(GET_COMMENTS_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(requestPatternBuilder);
        return requestPatternBuilder;
    }

    @SneakyThrows
    private RequestPatternBuilder getQuestionInformationRequestPatternBuilder() {
        RequestPatternBuilder requestPatternBuilder = getRequestedFor(
                urlPathMatching(String.format(GET_QUESTION_INFORMATION_URL_FORMAT, DEFAULT_QUESTION_ID)));
        addCommonQueryParams(requestPatternBuilder);
        return requestPatternBuilder;
    }

    private void addCommonQueryParams(MappingBuilder builder) {
        builder.withQueryParam(SITE_QUERY_PARAM_NAME, equalTo(SITE_QUERY_PARAM_VALUE))
                .withQueryParam(FILTER_QUERY_PARAM_NAME, equalTo(FILTER_QUERY_PARAM_VALUE))
                .withQueryParam(
                        KEY_QUERY_PARAM_NAME,
                        equalTo(scrapperConfig.stackOverflow().key()))
                .withQueryParam(
                        ACCESS_TOKEN_QUERY_PARAM_NAME,
                        equalTo(scrapperConfig.stackOverflow().accessToken()));
    }

    private void addCommonQueryParams(RequestPatternBuilder builder) {
        builder.withQueryParam(SITE_QUERY_PARAM_NAME, equalTo(SITE_QUERY_PARAM_VALUE))
                .withQueryParam(FILTER_QUERY_PARAM_NAME, equalTo(FILTER_QUERY_PARAM_VALUE))
                .withQueryParam(
                        KEY_QUERY_PARAM_NAME,
                        equalTo(scrapperConfig.stackOverflow().key()))
                .withQueryParam(
                        ACCESS_TOKEN_QUERY_PARAM_NAME,
                        equalTo(scrapperConfig.stackOverflow().accessToken()));
    }

    @SneakyThrows
    private ResponseDefinitionBuilder getDefaultCorrectResponse() {
        return aResponse()
                .withStatus(HttpStatus.OK_200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(DEFAULT_STACKOVERFLOW_RESPONSE_DTO))
                .withFixedDelay(getFixedDelayForStackoverflowClient());
    }

    private int getFixedDelayForStackoverflowClient() {
        return (int) (stackoverflowClientProperties.responseTimeoutMillis() / 2);
    }
}
