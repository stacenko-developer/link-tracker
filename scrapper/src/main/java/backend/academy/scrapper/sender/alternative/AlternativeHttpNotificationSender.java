package backend.academy.scrapper.sender.alternative;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.sender.base.BaseHttpNotificationSender;
import backend.academy.scrapper.service.client.BotClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlternativeHttpNotificationSender extends BaseHttpNotificationSender
        implements AlternativeNotificationSender {

    public AlternativeHttpNotificationSender(BotClientService botServiceClient) {
        super(botServiceClient);
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
