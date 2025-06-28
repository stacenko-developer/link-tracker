package backend.academy.scrapper.dao.tag;

import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_UPDATED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LINK_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_NAME_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.URL_PARAM;

import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.dao.tag.service.JpaTagDaoService;
import backend.academy.scrapper.dao.tag.service.TagDaoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public abstract class TagDaoServiceTest extends TestConfiguration {

    private static final String DEFAULT_TAG_NAME = "tag name";

    @Autowired
    protected TagDaoService tagDaoService;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    public void findByNameWithCorrectArguments_ShouldFindTag() {
        Tag createdTag = jdbcClient
                .sql("INSERT INTO tr_tag(name) VALUES (:name) RETURNING id, name")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .query(Tag.class)
                .single();

        Tag foundedTag = tagDaoService.findByName(DEFAULT_TAG_NAME);

        Assertions.assertNotNull(foundedTag);
        Assertions.assertEquals(createdTag.id(), foundedTag.id());
        Assertions.assertEquals(createdTag.name(), foundedTag.name());
    }

    @Test
    @Rollback
    @Transactional
    public void findByWithNotAddedName_ShouldReturnNull() {
        String incorrectTagName = "incorrect tag name";

        jdbcClient
                .sql("INSERT INTO tr_tag(name) VALUES (:name)")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .update();

        Tag foundedTag = tagDaoService.findByName(incorrectTagName);

        Assertions.assertNull(foundedTag);
    }

    @Test
    @Rollback
    @Transactional
    public void createTagWithCorrectArguments_ShouldCreateTag() {
        Tag tag = new Tag();
        tag.name(DEFAULT_TAG_NAME);

        Tag createdTag = tagDaoService.createTag(tag);

        if (tagDaoService instanceof JpaTagDaoService) {
            entityManager.flush();
        }

        Tag foundedTag = jdbcClient
                .sql("SELECT * FROM tr_tag WHERE name = :name")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .query(Tag.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundedTag);
        Assertions.assertEquals(createdTag.id(), foundedTag.id());
        Assertions.assertEquals(createdTag.name(), foundedTag.name());
    }

    @Test
    @Rollback
    @Transactional
    public void removeOrphanedTags_ShouldRemoveOrphanedTags() {
        jdbcClient
                .sql("INSERT INTO tr_tag(name) VALUES (:name)")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .update();

        tagDaoService.removeOrphanedTags();

        if (tagDaoService instanceof JpaTagDaoService) {
            entityManager.flush();
        }

        Tag foundTag = jdbcClient
                .sql("SELECT * FROM tr_tag WHERE name = :name")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .query(Tag.class)
                .optional()
                .orElse(null);

        Assertions.assertNull(foundTag);
    }

    @Test
    @Rollback
    @Transactional
    public void removeNotOrphanedTags_ShouldNotRemoveTags() {
        Long chatId = 1L;
        String url = "url";
        Long lastUpdatedAt = 0L;

        Long tagId = jdbcClient
                .sql("INSERT INTO tr_tag(name) VALUES (:name) RETURNING id")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
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
                .sql("INSERT INTO tr_chat_link_tag(chat_id, link_id, tag_id) VALUES (:chat_id, :link_id, :tag_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, linkId)
                .param(TAG_ID_PARAM, tagId)
                .update();

        tagDaoService.removeOrphanedTags();

        if (tagDaoService instanceof JpaTagDaoService) {
            entityManager.flush();
        }

        Tag foundTag = jdbcClient
                .sql("SELECT * FROM tr_tag WHERE name = :name")
                .param(TAG_NAME_PARAM, DEFAULT_TAG_NAME)
                .query(Tag.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundTag);
        Assertions.assertEquals(tagId, foundTag.id());
        Assertions.assertEquals(DEFAULT_TAG_NAME, foundTag.name());
    }
}
