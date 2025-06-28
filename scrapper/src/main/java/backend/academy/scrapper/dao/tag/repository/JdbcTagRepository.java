package backend.academy.scrapper.dao.tag.repository;

import static backend.academy.scrapper.constants.DatabaseConstants.TAG_NAME_PARAM;

import backend.academy.scrapper.dao.tag.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcTagRepository {

    private final JdbcClient jdbcClient;

    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return jdbcClient
                .sql("SELECT * FROM tr_tag WHERE name = :name")
                .param(TAG_NAME_PARAM, name)
                .query(Tag.class)
                .optional()
                .orElse(null);
    }

    @Transactional
    public Tag create(Tag tag) {
        return jdbcClient
                .sql("INSERT INTO tr_tag(name) VALUES (:name) RETURNING id, name")
                .param(TAG_NAME_PARAM, tag.name())
                .query(Tag.class)
                .single();
    }

    @Transactional
    public void removeOrphanedTags() {
        jdbcClient
                .sql(
                        """
                    DELETE FROM tr_tag t WHERE NOT EXISTS (
                        SELECT 1
                        FROM tr_chat_link_tag clt
                        WHERE clt.tag_id = t.id)
                     """)
                .update();
    }
}
