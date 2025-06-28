package backend.academy.scrapper.sender.primary;

import static backend.academy.scrapper.constants.ResilienceConstValues.DIGEST_UPDATE_EXCEPTION_FALLBACK_MESSAGE;
import static backend.academy.scrapper.constants.ResilienceConstValues.HTTP_NOTIFICATION_SENDER_CIRCUIT_BREAKER;
import static backend.academy.scrapper.constants.ResilienceConstValues.HTTP_NOTIFICATION_SENDER_RETRY;
import static backend.academy.scrapper.constants.ResilienceConstValues.IMMEDIATE_UPDATE_EXCEPTION_FALLBACK_MESSAGE;
import static backend.academy.scrapper.constants.ResilienceConstValues.SEND_DIGEST_UPDATE_FALLBACK_METH0D;
import static backend.academy.scrapper.constants.ResilienceConstValues.SEND_IMMEDIATE_UPDATE_FALLBACK_METH0D;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.sender.alternative.AlternativeNotificationSender;
import backend.academy.scrapper.sender.base.BaseHttpNotificationSender;
import backend.academy.scrapper.service.client.BotClientService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpNotificationSender extends BaseHttpNotificationSender implements NotificationSender {

    private final AlternativeNotificationSender alternativeNotificationSender;

    public HttpNotificationSender(
            BotClientService botServiceClient, AlternativeNotificationSender alternativeNotificationSender) {
        super(botServiceClient);
        this.alternativeNotificationSender = alternativeNotificationSender;
    }

    @Override
    @Retry(name = HTTP_NOTIFICATION_SENDER_RETRY)
    @CircuitBreaker(
            name = HTTP_NOTIFICATION_SENDER_CIRCUIT_BREAKER,
            fallbackMethod = SEND_IMMEDIATE_UPDATE_FALLBACK_METH0D)
    public void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        super.sendImmediateLinkUpdate(immediateLinkUpdate);
    }

    @Override
    @Retry(name = HTTP_NOTIFICATION_SENDER_RETRY)
    @CircuitBreaker(
            name = HTTP_NOTIFICATION_SENDER_CIRCUIT_BREAKER,
            fallbackMethod = SEND_DIGEST_UPDATE_FALLBACK_METH0D)
    public void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate) {
        super.sendDigestLinkUpdate(digestLinkUpdate);
    }

    public void sendImmediateLinkUpdateFallback(ImmediateLinkUpdate immediateLinkUpdate, Throwable t) {
        log.error(IMMEDIATE_UPDATE_EXCEPTION_FALLBACK_MESSAGE, t);

        alternativeNotificationSender.sendImmediateLinkUpdate(immediateLinkUpdate);
    }

    public void sendDigestLinkUpdateFallback(DigestLinkUpdate digestLinkUpdate, Throwable t) {
        log.error(DIGEST_UPDATE_EXCEPTION_FALLBACK_MESSAGE, t);

        alternativeNotificationSender.sendDigestLinkUpdate(digestLinkUpdate);
    }
}
