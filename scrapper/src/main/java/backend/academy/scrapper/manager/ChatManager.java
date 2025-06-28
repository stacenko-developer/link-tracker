package backend.academy.scrapper.manager;

import backend.academy.scrapper.cache.cleaner.CacheCleaner;
import backend.academy.scrapper.client.bot.dto.DigestLinkUpdate;
import backend.academy.scrapper.dao.chatDigestState.service.ChatDigestStateService;
import backend.academy.scrapper.dto.ChatDto;
import backend.academy.scrapper.dto.request.chat.ChatSettingsRequest;
import backend.academy.scrapper.dto.response.chat.ChatResponse;
import backend.academy.scrapper.enums.NotificationMode;
import backend.academy.scrapper.sender.primary.NotificationSender;
import backend.academy.scrapper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatManager {

    private final ChatService chatService;
    private final CacheCleaner cacheCleaner;
    private final NotificationSender notificationSender;
    private final ChatDigestStateService chatDigestStateService;

    public void registerChat(Long id) {
        chatService.registerChat(id);

        cacheCleaner.cleanLinkTrackingCache(id);
    }

    public ChatResponse updateChatSettings(Long id, ChatSettingsRequest chatSettingsRequest) {
        ChatDto currentChat = chatService.getChatById(id);

        ChatDto chatDtoToUpdate =
                new ChatDto(id, NotificationMode.getNotificationModeByName(chatSettingsRequest.notificationModeCode()));

        ChatDto resultChat = chatService.updateChatSettings(id, chatDtoToUpdate);

        if (NotificationMode.DAILY_DIGEST.equals(currentChat.notificationMode())
                && !NotificationMode.DAILY_DIGEST.equals(resultChat.notificationMode())) {
            DigestLinkUpdate digestLinkUpdate = chatDigestStateService.getDigestState(id);

            if (digestLinkUpdate != null) {
                notificationSender.sendDigestLinkUpdate(digestLinkUpdate);
                chatDigestStateService.deleteDigestState(id);
            }
        }

        return resultChat.toChatResponse();
    }

    public ChatResponse getChatById(Long id) {
        ChatDto chatDto = chatService.getChatById(id);

        return chatDto.toChatResponse();
    }

    public void deleteChat(Long id) {
        ChatDto currentChat = chatService.getChatById(id);

        chatService.deleteChat(id);

        if (NotificationMode.DAILY_DIGEST.equals(currentChat.notificationMode())) {
            chatDigestStateService.deleteDigestState(id);
        }

        cacheCleaner.cleanLinkTrackingCache(id);
    }
}
