package backend.academy.scrapper.dao.chat.service;

import backend.academy.scrapper.dao.chat.entity.Chat;

public interface ChatDaoService {

    Chat save(Chat chat);

    Chat findChatById(Long id);

    void deleteChat(Long id);
}
