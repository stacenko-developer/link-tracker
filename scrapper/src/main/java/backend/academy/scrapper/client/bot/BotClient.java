package backend.academy.scrapper.client.bot;

import static backend.academy.scrapper.constants.APIConstValues.DIGEST_UPDATE_URL;
import static backend.academy.scrapper.constants.APIConstValues.IMMEDIATE_UPDATE_URL;

import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.client.bot.dto.ImmediateLinkUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {

    @PostExchange(IMMEDIATE_UPDATE_URL)
    ResponseEntity<Void> immediateUpdate(@RequestBody ImmediateLinkUpdate immediateLinkUpdate);

    @PostExchange(DIGEST_UPDATE_URL)
    ResponseEntity<Void> digestUpdate(@RequestBody DigestLinkUpdate linkUpdate);
}
