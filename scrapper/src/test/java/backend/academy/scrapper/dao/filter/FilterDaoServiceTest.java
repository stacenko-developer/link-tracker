package backend.academy.scrapper.dao.filter;

import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_KEY_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_VALUE_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_UPDATED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LINK_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.URL_PARAM;

import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.filter.service.FilterDaoService;
import backend.academy.scrapper.dao.filter.service.JpaFilterDaoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public abstract class FilterDaoServiceTest extends TestConfiguration {

    private static final String DEFAULT_KEY = "key";
    private static final String DEFAULT_VALUE = "value";

    @Autowired
    protected FilterDaoService filterDaoService;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    public void findByKeyAndValueWithCorrectArguments_ShouldFindFilter() {
        Filter createdFilter = jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value) RETURNING id, key, value")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .query(Filter.class)
                .single();

        Filter foundFilter = filterDaoService.findByKeyAndValue(DEFAULT_KEY, DEFAULT_VALUE);

        Assertions.assertNotNull(foundFilter);
        Assertions.assertEquals(createdFilter.id(), foundFilter.id());
        Assertions.assertEquals(createdFilter.key(), foundFilter.key());
        Assertions.assertEquals(createdFilter.value(), foundFilter.value());
    }

    @Test
    @Rollback
    @Transactional
    public void findByKeyAndValueWithNotAddedKey_ShouldReturnNull() {
        String incorrectKey = "incorrectKey";
        jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value)")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .update();

        Filter foundFilter = filterDaoService.findByKeyAndValue(incorrectKey, DEFAULT_VALUE);

        Assertions.assertNull(foundFilter);
    }

    @Test
    @Rollback
    @Transactional
    public void findByKeyAndValueWithNotAddedValue_ShouldReturnNull() {
        String incorrectValue = "incorrectValue";
        jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value)")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .update();

        Filter foundFilter = filterDaoService.findByKeyAndValue(DEFAULT_KEY, incorrectValue);

        Assertions.assertNull(foundFilter);
    }

    @Test
    @Rollback
    @Transactional
    public void findByKeyAndValueWithNullKey_ShouldReturnNull() {
        jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value)")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .update();

        Filter foundFilter = filterDaoService.findByKeyAndValue(null, DEFAULT_VALUE);

        Assertions.assertNull(foundFilter);
    }

    @Test
    @Rollback
    @Transactional
    public void findByKeyAndValueWithNullValue_ShouldReturnNull() {
        jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value)")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .update();

        Filter foundFilter = filterDaoService.findByKeyAndValue(DEFAULT_KEY, null);

        Assertions.assertNull(foundFilter);
    }

    @Test
    @Rollback
    @Transactional
    public void createFilterWithCorrectArguments_ShouldCreateFilter() {
        Filter filter = new Filter();
        filter.key(DEFAULT_KEY);
        filter.value(DEFAULT_VALUE);

        Filter createdFilter = filterDaoService.createFilter(filter);

        if (filterDaoService instanceof JpaFilterDaoService) {
            entityManager.flush();
        }

        Filter foundFilter = jdbcClient
                .sql("SELECT * FROM tr_filter WHERE key = :key AND value = :value")
                .param(FILTER_KEY_PARAM, filter.key())
                .param(FILTER_VALUE_PARAM, filter.value())
                .query(Filter.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundFilter);
        Assertions.assertEquals(createdFilter.id(), foundFilter.id());
        Assertions.assertEquals(createdFilter.key(), foundFilter.key());
        Assertions.assertEquals(createdFilter.value(), foundFilter.value());
    }

    @Test
    @Rollback
    @Transactional
    public void removeOrphanedFilters_ShouldRemoveOrphanedFilters() {
        jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value)")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .update();

        filterDaoService.removeOrphanedFilters();

        if (filterDaoService instanceof JpaFilterDaoService) {
            entityManager.flush();
        }

        Filter foundFilter = jdbcClient
                .sql("SELECT * FROM tr_filter WHERE key = :key AND value = :value")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .query(Filter.class)
                .optional()
                .orElse(null);

        Assertions.assertNull(foundFilter);
    }

    @Test
    @Rollback
    @Transactional
    public void removeNotOrphanedFilters_ShouldNotRemoveFilters() {
        Long chatId = 1L;
        String url = "url";
        Long lastUpdatedAt = 0L;

        Long filterId = jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value) RETURNING id")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .query(Long.class)
                .single();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, chatId)
                .update();
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at) VALUES (:url, :last_updated_at) RETURNING id")
                .param(URL_PARAM, url)
                .param(LAST_UPDATED_AT_PARAM, lastUpdatedAt)
                .query(Long.class)
                .single();

        jdbcClient
                .sql(
                        "INSERT INTO tr_chat_link_filter(chat_id, link_id, filter_id) VALUES (:chat_id, :link_id, :filter_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, linkId)
                .param(FILTER_ID_PARAM, filterId)
                .update();

        filterDaoService.removeOrphanedFilters();

        if (filterDaoService instanceof JpaFilterDaoService) {
            entityManager.flush();
        }

        Filter foundFilter = jdbcClient
                .sql("SELECT * FROM tr_filter WHERE key = :key AND value = :value")
                .param(FILTER_KEY_PARAM, DEFAULT_KEY)
                .param(FILTER_VALUE_PARAM, DEFAULT_VALUE)
                .query(Filter.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundFilter);
        Assertions.assertEquals(filterId, foundFilter.id());
        Assertions.assertEquals(DEFAULT_KEY, foundFilter.key());
        Assertions.assertEquals(DEFAULT_VALUE, foundFilter.value());
    }
}
