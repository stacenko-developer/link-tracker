package backend.academy.scrapper.dao.tag;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.tag.service.JpaTagDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=ORM"})
public class JpaTagDaoServiceTest extends TagDaoServiceTest {

    @Test
    public void ormAccessType_ShouldUseJpaImplementation() {
        assertInstanceOf(JpaTagDaoService.class, tagDaoService);
    }
}
