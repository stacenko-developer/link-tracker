package backend.academy.scrapper.sender.alternative;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.sender.base.BaseKafkaNotificationSender;
import backend.academy.scrapper.service.client.KafkaClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlternativeKafkaNotificationSender extends BaseKafkaNotificationSender
        implements AlternativeNotificationSender {

    public AlternativeKafkaNotificationSender(KafkaClientService kafkaClientService) {
        super(kafkaClientService);
    }

    @Override
    public void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        super.sendImmediateLinkUpdate(immediateLinkUpdate);
    }

    @Override
    public void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate) {
        super.sendDigestLinkUpdate(digestLinkUpdate);
    }
}
