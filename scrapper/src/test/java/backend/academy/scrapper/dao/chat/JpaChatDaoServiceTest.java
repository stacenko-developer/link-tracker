package backend.academy.scrapper.dao.chat;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.chat.service.JpaChatDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=ORM"})
public class JpaChatDaoServiceTest extends ChatDaoServiceTest {

    @Test
    public void ormAccessType_ShouldUseJpaImplementation() {
        assertInstanceOf(JpaChatDaoService.class, chatDaoService);
    }
}
