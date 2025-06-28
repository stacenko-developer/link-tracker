package backend.academy.scrapper.dao.chat;

import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chat.service.ChatDaoService;
import backend.academy.scrapper.dao.chat.service.JpaChatDaoService;
import backend.academy.scrapper.enums.NotificationMode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public abstract class ChatDaoServiceTest extends TestConfiguration {

    @Autowired
    protected ChatDaoService chatDaoService;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    public void addChatWithCorrectArguments_ShouldSave() {
        Chat chat = new Chat();
        Long chatId = 1L;
        NotificationMode notificationMode = NotificationMode.IMMEDIATE;

        chat.id(chatId);
        chat.notificationMode(notificationMode);

        Chat createdChat = chatDaoService.save(chat);

        if (chatDaoService instanceof JpaChatDaoService) {
            entityManager.flush();
        }

        assertNotNull(createdChat);
        assertEquals(chatId, createdChat.id());

        Chat foundChat = jdbcClient
                .sql("SELECT * FROM tr_chat WHERE id = :id")
                .param(ID_PARAM, chatId)
                .query(Chat.class)
                .optional()
                .orElse(null);

        assertNotNull(foundChat);
        assertEquals(chatId, foundChat.id());
        assertEquals(notificationMode, foundChat.notificationMode());
    }

    @Test
    @Rollback
    @Transactional
    public void findChatByIdWithCorrectArguments_ShouldFindChat() {
        Chat chat = new Chat();
        Long chatId = 1L;
        chat.id(chatId);

        Chat createdChat = jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id) RETURNING id")
                .param(ID_PARAM, chat.id())
                .query(Chat.class)
                .single();

        Chat foundChat = chatDaoService.findChatById(chatId);

        assertNotNull(foundChat);
        assertEquals(createdChat.id(), foundChat.id());
    }

    @Test
    @Rollback
    @Transactional
    public void findChatByNotAddedIdWithCorrectArguments_ShouldReturnNull() {
        Chat chat = new Chat();
        Long chatId = 1L;
        Long chatIdToFind = 2L;
        chat.id(chatId);

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, chat.id())
                .update();

        Chat foundChat = chatDaoService.findChatById(chatIdToFind);

        Assertions.assertNull(foundChat);
    }

    @Test
    @Rollback
    @Transactional
    public void deleteChatWithCorrectArguments_ShouldDeleteChat() {
        Chat chat = new Chat();
        Long chatId = 1L;
        chat.id(chatId);

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, chat.id())
                .update();

        chatDaoService.deleteChat(chatId);

        if (chatDaoService instanceof JpaChatDaoService) {
            entityManager.flush();
        }

        Chat removedChat = jdbcClient
                .sql("SELECT * FROM tr_chat WHERE id = :id")
                .param(ID_PARAM, chatId)
                .query(Chat.class)
                .optional()
                .orElse(null);

        Assertions.assertNull(removedChat);
    }
}
