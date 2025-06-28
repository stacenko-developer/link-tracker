package backend.academy.scrapper.sender.base;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.service.client.KafkaClientService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseKafkaNotificationSender {

    private final KafkaClientService kafkaClientService;

    protected void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        kafkaClientService.sendImmediateLinkUpdate(immediateLinkUpdate);
    }

    protected void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate) {
        kafkaClientService.sendDigestLinkUpdate(digestLinkUpdate);
    }
}
