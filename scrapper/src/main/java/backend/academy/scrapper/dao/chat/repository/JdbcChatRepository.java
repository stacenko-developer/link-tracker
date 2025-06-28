package backend.academy.scrapper.dao.chat.repository;

import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;

import backend.academy.scrapper.dao.chat.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcChatRepository {

    private final JdbcClient jdbcClient;

    @Transactional
    public Chat save(Chat chat) {
        return jdbcClient
                .sql(
                        """
                    INSERT INTO tr_chat(id, notification_mode) VALUES (:id, :notification_mode)
                    ON CONFLICT (id)
                    DO UPDATE SET
                        notification_mode = :notification_mode
                    RETURNING id, notification_mode
                    """)
                .param(ID_PARAM, chat.id())
                .param("notification_mode", chat.notificationMode().name())
                .query(Chat.class)
                .single();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Chat findById(Long id) {
        return jdbcClient
                .sql("SELECT * FROM tr_chat WHERE id = :id")
                .param(ID_PARAM, id)
                .query(Chat.class)
                .optional()
                .orElse(null);
    }

    @Transactional
    public void deleteById(Long id) {
        jdbcClient.sql("DELETE FROM tr_chat WHERE id = :id").param(ID_PARAM, id).update();
    }
}
