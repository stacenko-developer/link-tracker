package backend.academy.scrapper.dao.link;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.link.service.JpaLinkDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=ORM"})
public class JpaLinkDaoServiceTest extends LinkDaoServiceTest {

    @Test
    public void ormAccessType_ShouldUseJpaImplementation() {
        assertInstanceOf(JpaLinkDaoService.class, linkDaoService);
    }
}
