package backend.academy.scrapper.sender.base;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import backend.academy.scrapper.service.client.BotClientService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseHttpNotificationSender {

    private final BotClientService botServiceClient;

    protected void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate) {
        botServiceClient.immediateUpdate(immediateLinkUpdate);
    }

    protected void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate) {
        botServiceClient.digestUpdate(digestLinkUpdate);
    }
}
