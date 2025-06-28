package backend.academy.bot.client.scrapper;

import static backend.academy.bot.ConstValues.CHAT_API_BASE_URL;
import static backend.academy.bot.ConstValues.CHAT_ID_HEADER_NAME;
import static backend.academy.bot.ConstValues.DEFAULT_ADD_LINK_REQUEST;
import static backend.academy.bot.ConstValues.DEFAULT_API_ERROR_RESPONSE;
import static backend.academy.bot.ConstValues.DEFAULT_CHAT_RESPONSE;
import static backend.academy.bot.ConstValues.DEFAULT_CHAT_SETTINGS_REQUEST;
import static backend.academy.bot.ConstValues.DEFAULT_FIND_USER_LINKS_REQUEST;
import static backend.academy.bot.ConstValues.DEFAULT_LINK_RESPONSE;
import static backend.academy.bot.ConstValues.DEFAULT_LIST_LINKS_RESPONSE;
import static backend.academy.bot.ConstValues.DEFAULT_NOTIFICATION_MODES;
import static backend.academy.bot.ConstValues.DEFAULT_REMOVE_LINK_REQUEST;
import static backend.academy.bot.ConstValues.DEFAULT_TG_CHAT_ID;
import static backend.academy.bot.ConstValues.DEFAULT_URL;
import static backend.academy.bot.ConstValues.GET_ALL_USER_TRACKING_LINKS_URL;
import static backend.academy.bot.ConstValues.GET_NOTIFICATION_MODES_URL;
import static backend.academy.bot.ConstValues.LINKS_API_BASE_URL;
import static backend.academy.bot.constants.CacheConstValues.CACHE_KEY_PATTERN;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import backend.academy.bot.client.scrapper.dto.response.ChatResponse;
import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import backend.academy.bot.configuration.BotConfiguration;
import backend.academy.bot.configuration.client.scrapper.ScrapperClientProperties;
import backend.academy.bot.service.client.ScrapperClientService;
import backend.academy.common.dto.ResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import wiremock.org.eclipse.jetty.http.HttpStatus;

public class ScrapperClientServiceTest extends CommonClientServiceTest {

    private static final String CACHE_KEY_WITHOUT_TAGS_FORMAT = "%s::%s,null";
    private static final String OPERATION_WITH_CHAT_URL_FORMAT = "%s/%s";

    @Autowired
    private ScrapperClientService scrapperServiceClient;

    @Autowired
    private BotConfiguration botConfiguration;

    @Autowired
    private ScrapperClientProperties scrapperClientProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @SneakyThrows
    public void getAllUserTrackingLinksWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<ListLinksResponse> expectedResponse = new ResponseDto<>(DEFAULT_LIST_LINKS_RESPONSE, null);

        setUpWiremockGetAllUserTrackingLinksToReturnDefaultCorrectResponse();

        ResponseDto<ListLinksResponse> actualResponse =
                scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void addLinkWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<LinkResponse> expectedResponse = new ResponseDto<>(DEFAULT_LINK_RESPONSE, null);

        setUpWiremockAddLinkToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<LinkResponse> actualResponse =
                scrapperServiceClient.addLink(DEFAULT_TG_CHAT_ID, DEFAULT_ADD_LINK_REQUEST);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkMissingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void deleteLinkWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<LinkResponse> expectedResponse = new ResponseDto<>(DEFAULT_LINK_RESPONSE, null);

        setUpWiremockDeleteLinkToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<LinkResponse> actualResponse = scrapperServiceClient.deleteLink(DEFAULT_TG_CHAT_ID, DEFAULT_URL);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkMissingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void registerChatWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, null);

        setUpWiremockRegisterChatToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<Void> actualResponse = scrapperServiceClient.registerChat(DEFAULT_TG_CHAT_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkMissingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void updateChatSettingsWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<ChatResponse> expectedResponse = new ResponseDto<>(DEFAULT_CHAT_RESPONSE, null);

        setUpWiremockUpdateChatSettingsToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<ChatResponse> actualResponse =
                scrapperServiceClient.updateChatSettings(DEFAULT_TG_CHAT_ID, DEFAULT_CHAT_SETTINGS_REQUEST);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void getChatByIdWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<ChatResponse> expectedResponse = new ResponseDto<>(DEFAULT_CHAT_RESPONSE, null);

        setUpWiremockGetChatByIdToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<ChatResponse> actualResponse = scrapperServiceClient.getChatById(DEFAULT_TG_CHAT_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void getNotificationModesWithCorrectResponse_ShouldReturnCorrectContent() {
        ResponseDto<List<NotificationModeDto>> expectedResponse = new ResponseDto<>(DEFAULT_NOTIFICATION_MODES, null);

        setUpWiremockGetNotificationModesToReturnDefaultCorrectResponse();
        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<List<NotificationModeDto>> actualResponse = scrapperServiceClient.getNotificationModes();

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void getAllUserTrackingLinksWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(getAllUserTrackingLinksMappingBuilder().willReturn(getBadRequestResponse()));

        ResponseDto<ListLinksResponse> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<ListLinksResponse> actualResponse =
                scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkMissingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void addLinkWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(addLinkMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<LinkResponse> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<LinkResponse> actualResponse =
                scrapperServiceClient.addLink(DEFAULT_TG_CHAT_ID, DEFAULT_ADD_LINK_REQUEST);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void deleteLinkWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(deleteLinkMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<LinkResponse> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);
        ResponseDto<LinkResponse> actualResponse = scrapperServiceClient.deleteLink(DEFAULT_TG_CHAT_ID, DEFAULT_URL);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void registerChatWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(registerChatMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<Void> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        ResponseDto<Void> actualResponse = scrapperServiceClient.registerChat(DEFAULT_TG_CHAT_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void updateChatSettingsWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(updateChatSettingsMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<ChatResponse> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        ResponseDto<ChatResponse> actualResponse =
                scrapperServiceClient.updateChatSettings(DEFAULT_TG_CHAT_ID, DEFAULT_CHAT_SETTINGS_REQUEST);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void getChatByIdWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(getChatByIdMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<ChatResponse> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        ResponseDto<ChatResponse> actualResponse = scrapperServiceClient.getChatById(DEFAULT_TG_CHAT_ID);

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    public void getNotificationModesWithWebClientResponseException_ShouldReturnApiErrorResponse() {
        stubFor(getNotificationModesMappingBuilder().willReturn(getBadRequestResponse()));

        setDefaultValueInCache(DEFAULT_TG_CHAT_ID);

        ResponseDto<List<NotificationModeDto>> expectedResponse = new ResponseDto<>(null, DEFAULT_API_ERROR_RESPONSE);

        ResponseDto<List<NotificationModeDto>> actualResponse = scrapperServiceClient.getNotificationModes();

        Assertions.assertEquals(expectedResponse, actualResponse);

        checkContainingCache(DEFAULT_TG_CHAT_ID);
    }

    @Test
    @SneakyThrows
    public void getAllUserTrackingLinksWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetAllUserTrackingLinksToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null),
                getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void addLinkWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockAddLinkToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.addLink(DEFAULT_TG_CHAT_ID, DEFAULT_ADD_LINK_REQUEST),
                getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void deleteLinkWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockDeleteLinkToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.deleteLink(DEFAULT_TG_CHAT_ID, DEFAULT_URL),
                getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void registerChatWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockRegisterChatToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.registerChat(DEFAULT_TG_CHAT_ID), getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void updateChatSettingsWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockUpdateChatSettingsToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.updateChatSettings(DEFAULT_TG_CHAT_ID, DEFAULT_CHAT_SETTINGS_REQUEST),
                getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void getChatByIdWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetChatByIdToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.getChatById(DEFAULT_TG_CHAT_ID), getFixedDelayForScrapperClient());
    }

    @Test
    @SneakyThrows
    public void getNotificationModesWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockGetNotificationModesToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> scrapperServiceClient.getNotificationModes(), getFixedDelayForScrapperClient());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getAllUserTrackingLinksWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetAllUserTrackingLinksRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getAllUserTrackingLinksWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetAllUserTrackingLinksRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void addLinkWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeAddLinkRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void addLinkWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeAddLinkRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void deleteLinkWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeDeleteLinkRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void deleteLinkWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeDeleteLinkRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void registerChatWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeRegisterChatRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void registerChatWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeRegisterChatRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void updateChatSettingsWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeUpdateChatSettingsRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void updateChatSettingsWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeUpdateChatSettingsRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getChatByIdWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetChatByIdRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getChatByIdWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetChatByIdRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void getNotificationModesWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeGetNotificationModesRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void getNotificationModesWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeGetNotificationModesRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    private void executeGetAllUserTrackingLinksRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getAllUserTrackingLinksRequestPatternBuilder(),
                getAllUserTrackingLinksMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_LIST_LINKS_RESPONSE,
                () -> scrapperServiceClient.getAllUserTrackingLinks(DEFAULT_TG_CHAT_ID, null));
    }

    @SneakyThrows
    private void executeAddLinkRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                addLinkRequestPatternBuilder(),
                addLinkMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_LINK_RESPONSE,
                () -> scrapperServiceClient.addLink(DEFAULT_TG_CHAT_ID, DEFAULT_ADD_LINK_REQUEST));
    }

    @SneakyThrows
    private void executeDeleteLinkRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                deleteLinkRequestPatternBuilder(),
                deleteLinkMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_LINK_RESPONSE,
                () -> scrapperServiceClient.deleteLink(DEFAULT_TG_CHAT_ID, DEFAULT_URL));
    }

    @SneakyThrows
    private void executeRegisterChatRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                registerChatRequestPatternBuilder(),
                registerChatMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                null,
                () -> scrapperServiceClient.registerChat(DEFAULT_TG_CHAT_ID));
    }

    @SneakyThrows
    private void executeUpdateChatSettingsRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                updateChatSettingsRequestPatternBuilder(),
                updateChatSettingsMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_CHAT_RESPONSE,
                () -> scrapperServiceClient.updateChatSettings(DEFAULT_TG_CHAT_ID, DEFAULT_CHAT_SETTINGS_REQUEST));
    }

    @SneakyThrows
    private void executeGetChatByIdRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getChatByIdRequestPatternBuilder(),
                getChatByIdMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_CHAT_RESPONSE,
                () -> scrapperServiceClient.getChatById(DEFAULT_TG_CHAT_ID));
    }

    @SneakyThrows
    private void executeGetNotificationModesRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                getNotificationModesRequestPatternBuilder(),
                getNotificationModesMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                DEFAULT_NOTIFICATION_MODES,
                () -> scrapperServiceClient.getNotificationModes());
    }

    private void setDefaultValueInCache(Long chatId) {
        String key = String.format(CACHE_KEY_WITHOUT_TAGS_FORMAT, getBotCacheName(), chatId);

        ResponseDto<ListLinksResponse> value = new ResponseDto<>(DEFAULT_LIST_LINKS_RESPONSE, null);

        redisTemplate.opsForValue().set(key, value);
    }

    private void checkMissingCache(Long chatId) {
        String searchKeysPattern = String.format(CACHE_KEY_PATTERN, getBotCacheName(), chatId);
        int expectedKeysSize = 0;

        Set<String> keys = redisTemplate.keys(searchKeysPattern);

        Assertions.assertNotNull(keys);
        Assertions.assertEquals(expectedKeysSize, keys.size());
    }

    private void checkContainingCache(Long chatId) {
        String searchKeysPattern = String.format(CACHE_KEY_PATTERN, getBotCacheName(), chatId);
        Set<String> keys = redisTemplate.keys(searchKeysPattern);
        String expectedKey = String.format(CACHE_KEY_WITHOUT_TAGS_FORMAT, getBotCacheName(), chatId);
        int expectedKeysSize = 1;

        Assertions.assertNotNull(keys);
        Assertions.assertEquals(expectedKeysSize, keys.size());
        Assertions.assertTrue(keys.contains(expectedKey));

        ResponseDto<ListLinksResponse> expectedCacheValue = new ResponseDto<>(DEFAULT_LIST_LINKS_RESPONSE, null);
        ResponseDto<ListLinksResponse> actualValue =
                objectMapper.convertValue(redisTemplate.opsForValue().get(expectedKey), new TypeReference<>() {});

        Assertions.assertEquals(expectedCacheValue, actualValue);
    }

    private String getBotCacheName() {
        return botConfiguration.cache().botTrackingLinks();
    }

    @SneakyThrows
    private void setUpWiremockGetAllUserTrackingLinksToReturnDefaultCorrectResponse() {
        stubFor(getAllUserTrackingLinksMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_LIST_LINKS_RESPONSE))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockAddLinkToReturnDefaultCorrectResponse() {
        stubFor(addLinkMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_LINK_RESPONSE))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockDeleteLinkToReturnDefaultCorrectResponse() {
        stubFor(deleteLinkMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_LINK_RESPONSE))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockRegisterChatToReturnDefaultCorrectResponse() {
        stubFor(registerChatMappingBuilder()
                .willReturn(
                        aResponse().withStatus(HttpStatus.OK_200).withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockUpdateChatSettingsToReturnDefaultCorrectResponse() {
        stubFor(updateChatSettingsMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_CHAT_RESPONSE))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockGetChatByIdToReturnDefaultCorrectResponse() {
        stubFor(getChatByIdMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_CHAT_RESPONSE))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private void setUpWiremockGetNotificationModesToReturnDefaultCorrectResponse() {
        stubFor(getNotificationModesMappingBuilder()
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK_200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(DEFAULT_NOTIFICATION_MODES))
                        .withFixedDelay(getFixedDelayForScrapperClient())));
    }

    @SneakyThrows
    private MappingBuilder getAllUserTrackingLinksMappingBuilder() {
        return post(urlPathMatching(GET_ALL_USER_TRACKING_LINKS_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_FIND_USER_LINKS_REQUEST)));
    }

    @SneakyThrows
    private MappingBuilder addLinkMappingBuilder() {
        return post(urlPathMatching(LINKS_API_BASE_URL))
                .withHeader(CHAT_ID_HEADER_NAME, equalTo(String.valueOf(DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_ADD_LINK_REQUEST)));
    }

    @SneakyThrows
    private MappingBuilder deleteLinkMappingBuilder() {
        return delete(urlPathMatching(LINKS_API_BASE_URL))
                .withHeader(CHAT_ID_HEADER_NAME, equalTo(String.valueOf(DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_REMOVE_LINK_REQUEST)));
    }

    @SneakyThrows
    private MappingBuilder registerChatMappingBuilder() {
        return post(
                urlPathMatching(String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)));
    }

    @SneakyThrows
    private MappingBuilder updateChatSettingsMappingBuilder() {
        return put(urlPathMatching(
                        String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_CHAT_SETTINGS_REQUEST)));
    }

    @SneakyThrows
    private MappingBuilder getChatByIdMappingBuilder() {
        return get(
                urlPathMatching(String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)));
    }

    @SneakyThrows
    private MappingBuilder getNotificationModesMappingBuilder() {
        return get(urlPathMatching(GET_NOTIFICATION_MODES_URL));
    }

    @SneakyThrows
    private RequestPatternBuilder getAllUserTrackingLinksRequestPatternBuilder() {
        return postRequestedFor(urlPathMatching(GET_ALL_USER_TRACKING_LINKS_URL))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_FIND_USER_LINKS_REQUEST)));
    }

    @SneakyThrows
    private RequestPatternBuilder addLinkRequestPatternBuilder() {
        return postRequestedFor(urlPathMatching(LINKS_API_BASE_URL))
                .withHeader(CHAT_ID_HEADER_NAME, equalTo(String.valueOf(DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_ADD_LINK_REQUEST)));
    }

    @SneakyThrows
    private RequestPatternBuilder deleteLinkRequestPatternBuilder() {
        return deleteRequestedFor(urlPathMatching(LINKS_API_BASE_URL))
                .withHeader(CHAT_ID_HEADER_NAME, equalTo(String.valueOf(DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_REMOVE_LINK_REQUEST)));
    }

    @SneakyThrows
    private RequestPatternBuilder registerChatRequestPatternBuilder() {
        return postRequestedFor(
                urlPathMatching(String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)));
    }

    @SneakyThrows
    private RequestPatternBuilder updateChatSettingsRequestPatternBuilder() {
        return putRequestedFor(urlPathMatching(
                        String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(DEFAULT_CHAT_SETTINGS_REQUEST)));
    }

    @SneakyThrows
    private RequestPatternBuilder getChatByIdRequestPatternBuilder() {
        return getRequestedFor(
                urlPathMatching(String.format(OPERATION_WITH_CHAT_URL_FORMAT, CHAT_API_BASE_URL, DEFAULT_TG_CHAT_ID)));
    }

    @SneakyThrows
    private RequestPatternBuilder getNotificationModesRequestPatternBuilder() {
        return getRequestedFor(urlPathMatching(GET_NOTIFICATION_MODES_URL));
    }

    private int getFixedDelayForScrapperClient() {
        return (int) (scrapperClientProperties.responseTimeoutMillis() / DEFAULT_RATION_FOR_FIXED_DELAY);
    }
}
