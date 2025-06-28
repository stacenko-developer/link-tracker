package backend.academy.scrapper.dao.tag;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.tag.service.JdbcTagDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=SQL"})
public class JdbcTagDaoServiceTest extends TagDaoServiceTest {

    @Test
    public void sqlAccessType_ShouldUseJdbcImplementation() {
        assertInstanceOf(JdbcTagDaoService.class, tagDaoService);
    }
}
