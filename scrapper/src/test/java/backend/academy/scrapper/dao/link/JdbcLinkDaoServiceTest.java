package backend.academy.scrapper.dao.link;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.link.service.JdbcLinkDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=SQL"})
public class JdbcLinkDaoServiceTest extends LinkDaoServiceTest {

    @Test
    public void sqlAccessType_ShouldUseJdbcImplementation() {
        assertInstanceOf(JdbcLinkDaoService.class, linkDaoService);
    }
}
