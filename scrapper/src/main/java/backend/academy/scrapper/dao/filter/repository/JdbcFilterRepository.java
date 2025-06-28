package backend.academy.scrapper.dao.filter.repository;

import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_KEY_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_VALUE_PARAM;

import backend.academy.scrapper.dao.filter.entity.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcFilterRepository {

    private final JdbcClient jdbcClient;

    @Transactional(readOnly = true)
    public Filter findByKeyAndValue(String key, String value) {
        return jdbcClient
                .sql("SELECT * FROM tr_filter WHERE key = :key AND value = :value")
                .param(FILTER_KEY_PARAM, key)
                .param(FILTER_VALUE_PARAM, value)
                .query(Filter.class)
                .optional()
                .orElse(null);
    }

    @Transactional
    public Filter create(Filter filter) {
        return jdbcClient
                .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value) RETURNING id, key, value")
                .param(FILTER_KEY_PARAM, filter.key())
                .param(FILTER_VALUE_PARAM, filter.value())
                .query(Filter.class)
                .optional()
                .orElse(null);
    }

    @Transactional
    public void removeOrphanedFilters() {
        jdbcClient
                .sql(
                        """
                DELETE FROM tr_filter f WHERE NOT EXISTS (
                        SELECT 1
                        FROM tr_chat_link_filter clf
                        WHERE clf.filter_id = f.id
                        )
                        """)
                .update();
    }
}
