package backend.academy.scrapper.sender;

import static backend.academy.scrapper.ConstValues.DEFAULT_DIGEST_LINK_UPDATE;
import static backend.academy.scrapper.ConstValues.DEFAULT_IMMEDIATE_LINK_UPDATE;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import backend.academy.scrapper.client.CommonClientServiceTest;
import backend.academy.scrapper.sender.alternative.AlternativeNotificationSender;
import backend.academy.scrapper.sender.primary.NotificationSender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestPropertySource(properties = {"app.message-transport=http"})
public class HttpNotificationSenderTest extends CommonClientServiceTest {

    @MockitoBean
    private AlternativeNotificationSender alternativeNotificationSender;

    @Autowired
    private NotificationSender notificationSender;

    @Test
    public void sendImmediateLinkUpdateWithError_ShouldSendAlternativeMethod() {
        setUpWiremockImmediateUpdateToReturnServerError();

        notificationSender.sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);

        verify(alternativeNotificationSender).sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);
    }

    @Test
    public void sendDigestLinkUpdateWithError_ShouldSendAlternativeMethod() {
        setUpWiremockDigestUpdateToReturnServerError();

        notificationSender.sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);

        verify(alternativeNotificationSender).sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);
    }

    @Test
    @SneakyThrows
    public void sendImmediateLinkUpdateWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockImmediateUpdateToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> notificationSender.sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE),
                getFixedDelayForBotClient(),
                scrapperConfig.resilienceInstances().httpNotificationSender().circuitBreaker());

        verify(alternativeNotificationSender).sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);
    }

    @Test
    @SneakyThrows
    public void sendDigestLinkUpdateWithOpenCircuitBreaker_ShouldDoFallbackMethod() {
        setUpWiremockDigestUpdateToReturnDefaultCorrectResponse();

        verifyCircuitBreakerRejectsCall(
                () -> notificationSender.sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE),
                getFixedDelayForBotClient(),
                scrapperConfig.resilienceInstances().httpNotificationSender().circuitBreaker());

        verify(alternativeNotificationSender).sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void immediateUpdateWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeImmediateUpdateRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void immediateUpdateWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeImmediateUpdateRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getServerErrorCodes")
    public void digestUpdateWithServerError_ShouldDoRetry(int httpStatusCode) {
        executeDigestUpdateRetryTest(httpStatusCode, true);
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getClientErrorCodes")
    public void digestUpdateWithClientError_ShouldNotDoRetry(int httpStatusCode) {
        executeDigestUpdateRetryTest(httpStatusCode, false);
    }

    @SneakyThrows
    private void executeImmediateUpdateRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                immediateUpdateRequestPatternBuilder(),
                immediateUpdateMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                () -> notificationSender.sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE),
                scrapperConfig.resilienceInstances().github().retry());

        verify(alternativeNotificationSender, never()).sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);
    }

    @SneakyThrows
    private void executeDigestUpdateRetryTest(int httpStatusCode, boolean shouldRetry) {
        executeRetryTest(
                digestUpdateRequestPatternBuilder(),
                digestUpdateMappingBuilder(),
                httpStatusCode,
                shouldRetry,
                () -> notificationSender.sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE),
                scrapperConfig.resilienceInstances().github().retry());

        verify(alternativeNotificationSender, never()).sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);
    }
}
