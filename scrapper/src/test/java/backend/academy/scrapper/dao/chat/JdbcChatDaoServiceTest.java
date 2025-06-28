package backend.academy.scrapper.dao.chat;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.chat.service.JdbcChatDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=SQL"})
public class JdbcChatDaoServiceTest extends ChatDaoServiceTest {

    @Test
    public void sqlAccessType_ShouldUseJdbcImplementation() {
        assertInstanceOf(JdbcChatDaoService.class, chatDaoService);
    }
}
