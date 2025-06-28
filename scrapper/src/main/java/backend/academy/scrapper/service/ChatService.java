package backend.academy.scrapper.service;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.service.ChatDaoService;
import backend.academy.scrapper.dto.ChatDto;
import backend.academy.scrapper.enums.NotificationMode;
import backend.academy.scrapper.exception.chat.ChatHasAlreadyRegisteredException;
import backend.academy.scrapper.exception.chat.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatDaoService chatDaoService;

    @Transactional
    public void registerChat(Long id) {
        Chat chat = chatDaoService.findChatById(id);

        if (chat != null) {
            throw new ChatHasAlreadyRegisteredException(id);
        }

        Chat chatToRegister = new Chat();
        chatToRegister.id(id);
        chatToRegister.notificationMode(NotificationMode.IMMEDIATE);

        chatDaoService.save(chatToRegister);
    }

    @Transactional
    public ChatDto updateChatSettings(Long id, ChatDto chatDto) {
        Chat chat = chatDaoService.findChatById(id);

        if (chat == null) {
            throw new ChatNotFoundException(id);
        }

        chat.notificationMode(chatDto.notificationMode());

        chatDaoService.save(chat);

        return new ChatDto(chat.id(), chat.notificationMode());
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public ChatDto getChatById(Long id) {
        Chat chat = chatDaoService.findChatById(id);

        if (chat == null) {
            throw new ChatNotFoundException(id);
        }

        return new ChatDto(id, chat.notificationMode());
    }

    @Transactional
    public void deleteChat(Long id) {
        Chat chat = chatDaoService.findChatById(id);

        if (chat == null) {
            throw new ChatNotFoundException(id);
        }

        chatDaoService.deleteChat(id);
    }
}
