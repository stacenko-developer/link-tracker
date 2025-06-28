package backend.academy.scrapper.sender.primary;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;

public interface NotificationSender {

    void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate);

    void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate);
}
