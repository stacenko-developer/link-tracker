package backend.academy.scrapper.dto;

import backend.academy.scrapper.dto.response.chat.ChatResponse;
import backend.academy.scrapper.enums.NotificationMode;

public record ChatDto(Long chatId, NotificationMode notificationMode) {

    public ChatResponse toChatResponse() {
        return new ChatResponse(
                chatId,
                new NotificationModeDto(
                        notificationMode.name(), notificationMode.value(), notificationMode.description()));
    }
}
