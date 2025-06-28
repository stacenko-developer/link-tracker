package backend.academy.scrapper.sender.alternative;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;

public interface AlternativeNotificationSender {

    void sendImmediateLinkUpdate(ImmediateLinkUpdate immediateLinkUpdate);

    void sendDigestLinkUpdate(DigestLinkUpdate digestLinkUpdate);
}
