package backend.academy.scrapper.dao.filter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import backend.academy.scrapper.dao.filter.service.JdbcFilterDaoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.access-type=SQL"})
public class JdbcFilterDaoServiceTest extends FilterDaoServiceTest {

    @Test
    public void sqlAccessType_ShouldUseJdbcImplementation() {
        assertInstanceOf(JdbcFilterDaoService.class, filterDaoService);
    }
}
