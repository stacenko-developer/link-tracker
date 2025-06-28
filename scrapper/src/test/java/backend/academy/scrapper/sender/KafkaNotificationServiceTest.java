package backend.academy.scrapper.sender;

import static backend.academy.scrapper.ConstValues.DEFAULT_DIGEST_LINK_UPDATE;
import static backend.academy.scrapper.ConstValues.DEFAULT_IMMEDIATE_LINK_UPDATE;
import static org.mockito.Mockito.doThrow;

import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.configuration.ScrapperConfig;
import backend.academy.scrapper.sender.alternative.AlternativeNotificationSender;
import backend.academy.scrapper.sender.primary.NotificationSender;
import org.apache.kafka.common.errors.InterruptException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestPropertySource(properties = {"app.message-transport=kafka"})
public class KafkaNotificationServiceTest extends TestConfiguration {

    @MockitoBean
    private KafkaTemplate<String, ImmediateLinkUpdate> immediateLinkUpdateKafkaTemplate;

    @MockitoBean
    private KafkaTemplate<String, DigestLinkUpdate> digestLinkUpdateKafkaTemplate;

    @MockitoBean
    private AlternativeNotificationSender alternativeNotificationSender;

    @Autowired
    private NotificationSender notificationSender;

    @Autowired
    private ScrapperConfig scrapperConfig;

    @Test
    public void sendImmediateLinkUpdateWithError_ShouldSendAlternativeMethod() {
        doThrow(new InterruptException(""))
                .when(immediateLinkUpdateKafkaTemplate)
                .send(scrapperConfig.kafkaTopics().immediateLinkUpdate(), DEFAULT_IMMEDIATE_LINK_UPDATE);

        notificationSender.sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);

        Mockito.verify(alternativeNotificationSender).sendImmediateLinkUpdate(DEFAULT_IMMEDIATE_LINK_UPDATE);
    }

    @Test
    public void sendDigestLinkUpdateWithError_ShouldSendAlternativeMethod() {
        doThrow(new InterruptException(""))
                .when(digestLinkUpdateKafkaTemplate)
                .send(scrapperConfig.kafkaTopics().digestLinkUpdate(), DEFAULT_DIGEST_LINK_UPDATE);

        notificationSender.sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);

        Mockito.verify(alternativeNotificationSender).sendDigestLinkUpdate(DEFAULT_DIGEST_LINK_UPDATE);
    }
}
