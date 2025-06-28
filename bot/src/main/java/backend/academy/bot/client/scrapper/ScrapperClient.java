package backend.academy.bot.client.scrapper;

import static backend.academy.bot.constants.APIConstValues.LINKS_API_BASE_URL;
import static backend.academy.bot.constants.APIConstValues.LINKS_SEARCH_URL;
import static backend.academy.bot.constants.APIConstValues.NOTIFICATION_MODE_URL;
import static backend.academy.bot.constants.APIConstValues.OPERATION_WITH_CHAT_URL;
import static backend.academy.bot.constants.APIConstValues.TG_CHAT_ID_HEADER;

import backend.academy.bot.client.scrapper.dto.NotificationModeDto;
import backend.academy.bot.client.scrapper.dto.request.AddLinkRequest;
import backend.academy.bot.client.scrapper.dto.request.ChatSettingsRequest;
import backend.academy.bot.client.scrapper.dto.request.FindUserLinksRequest;
import backend.academy.bot.client.scrapper.dto.request.RemoveLinkRequest;
import backend.academy.bot.client.scrapper.dto.response.ChatResponse;
import backend.academy.bot.client.scrapper.dto.response.LinkResponse;
import backend.academy.bot.client.scrapper.dto.response.ListLinksResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

public interface ScrapperClient {

    @PostExchange(LINKS_SEARCH_URL)
    ResponseEntity<ListLinksResponse> getAllUserTrackingLinks(@RequestBody FindUserLinksRequest findUserLinksRequest);

    @PostExchange(LINKS_API_BASE_URL)
    ResponseEntity<LinkResponse> addLink(
            @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody AddLinkRequest addLinkRequest);

    @DeleteExchange(LINKS_API_BASE_URL)
    ResponseEntity<LinkResponse> deleteLink(
            @RequestHeader(TG_CHAT_ID_HEADER) Long tgChatId, @RequestBody RemoveLinkRequest removeLinkRequest);

    @PostExchange(OPERATION_WITH_CHAT_URL)
    ResponseEntity<Void> registerChat(@PathVariable Long id);

    @PutExchange(OPERATION_WITH_CHAT_URL)
    ResponseEntity<ChatResponse> updateChatSettings(
            @PathVariable Long id, @RequestBody ChatSettingsRequest chatSettingsRequest);

    @GetExchange(OPERATION_WITH_CHAT_URL)
    ResponseEntity<ChatResponse> getChatById(@PathVariable Long id);

    @GetExchange(NOTIFICATION_MODE_URL)
    ResponseEntity<List<NotificationModeDto>> getNotificationModes();
}
