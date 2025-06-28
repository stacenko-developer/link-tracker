package backend.academy.scrapper.dao.filter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.filter.service.JpaFilterDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=ORM"})
public class JpaFilterDaoServiceTest extends FilterDaoServiceTest {

    @Test
    public void ormAccessType_ShouldUseJpaImplementation() {
        assertInstanceOf(JpaFilterDaoService.class, filterDaoService);
    }
}
