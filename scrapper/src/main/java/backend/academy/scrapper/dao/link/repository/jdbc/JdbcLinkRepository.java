package backend.academy.scrapper.dao.link.repository.jdbc;

import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_IDS_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_KEY_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_VALUE_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_TRACKED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_UPDATED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LIMIT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LINK_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_NAMES_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_NAME_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.URL_PARAM;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.tag.entity.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcLinkRepository {

    private final JdbcClient jdbcClient;

    @Transactional(readOnly = true)
    public List<Link> getAllUserTrackingLinks(Long chatId, List<String> tagNames) {
        StringBuilder sql = new StringBuilder(
                """
        WITH filtered_links AS (
            SELECT
                l.id,
                l.url,
                l.last_updated_at,
                l.last_tracked_at
            FROM tr_link l
            JOIN tr_chat_link cl ON cl.link_id = l.id
        """);

        if (tagNames != null && !tagNames.isEmpty()) {
            sql.append(
                    """
                LEFT JOIN tr_chat_link_tag clt ON clt.chat_id = cl.chat_id AND clt.link_id = l.id
                LEFT JOIN tr_tag t ON t.id = clt.tag_id
            """);
        }

        sql.append("""
            WHERE cl.chat_id = :chat_id
        """);

        if (tagNames != null && !tagNames.isEmpty()) {
            sql.append(" AND t.name = ANY(CAST(:tag_names AS text[]))");
        }

        sql.append(
                """
        )
        SELECT
            fl.id AS link_id,
            fl.url AS link_url,
            fl.last_updated_at AS link_last_updated_at,
            fl.last_tracked_at AS link_last_tracked_at,
            c.id AS chat_id,
            c.notification_mode AS chat_notification_mode,
            f.id AS filter_id,
            f.key AS filter_key,
            f.value AS filter_value,
            t.id AS tag_id,
            t.name AS tag_name
        FROM filtered_links fl
            LEFT JOIN tr_chat_link cl ON cl.link_id = fl.id
            LEFT JOIN tr_chat c ON cl.chat_id = c.id
            LEFT JOIN tr_chat_link_filter clf ON clf.link_id = fl.id AND cl.chat_id = clf.chat_id
            LEFT JOIN tr_chat_link_tag clt ON clt.link_id = fl.id AND cl.chat_id = clt.chat_id
            LEFT JOIN tr_filter f ON f.id = clf.filter_id
            LEFT JOIN tr_tag t ON t.id = clt.tag_id
        """);

        String[] tags = (tagNames == null || tagNames.isEmpty()) ? null : tagNames.toArray(String[]::new);

        return jdbcClient
                .sql(sql.toString())
                .param(CHAT_ID_PARAM, chatId)
                .param(TAG_NAMES_PARAM, tags)
                .query(new LinkRowMapper())
                .stream()
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public Link findByUrlAndChatId(String url, Long chatId) {
        String sql =
                """
        WITH filtered_links AS (
            SELECT
                l.id,
                l.url,
                l.last_updated_at,
                l.last_tracked_at
            FROM tr_link l
                JOIN tr_chat_link cl ON cl.link_id = l.id
            WHERE l.url = :url AND cl.chat_id = :chat_id
        )
        SELECT
            fl.id AS link_id,
            fl.url AS link_url,
            fl.last_updated_at AS link_last_updated_at,
            fl.last_tracked_at AS link_last_tracked_at,
            c.id AS chat_id,
            c.notification_mode AS chat_notification_mode,
            f.id AS filter_id,
            f.key AS filter_key,
            f.value AS filter_value,
            t.id AS tag_id,
            t.name AS tag_name
        FROM filtered_links fl
            LEFT JOIN tr_chat_link cl ON cl.link_id = fl.id
            LEFT JOIN tr_chat c ON cl.chat_id = c.id
            LEFT JOIN tr_chat_link_filter clf ON clf.link_id = fl.id AND cl.chat_id = clf.chat_id
            LEFT JOIN tr_chat_link_tag clt ON clt.link_id = fl.id AND cl.chat_id = clt.chat_id
            LEFT JOIN tr_filter f ON f.id = clf.filter_id
            LEFT JOIN tr_tag t ON t.id = clt.tag_id
        """;

        List<Link> rows =
                jdbcClient
                        .sql(sql)
                        .param(URL_PARAM, url)
                        .param(CHAT_ID_PARAM, chatId)
                        .query(new LinkRowMapper())
                        .stream()
                        .distinct()
                        .toList();

        return rows.isEmpty() ? null : rows.getFirst();
    }

    @Transactional(readOnly = true)
    public List<Link> findAll(Long lastTrackedAt, Integer limit) {
        String sql =
                """
        WITH filtered_links AS (
            SELECT
                l.id,
                l.url,
                l.last_updated_at,
                l.last_tracked_at
            FROM tr_link l
            WHERE l.last_tracked_at IS NULL OR l.last_tracked_at < :last_tracked_at
            LIMIT :limit
        )
        SELECT
            fl.id AS link_id,
            fl.url AS link_url,
            fl.last_updated_at AS link_last_updated_at,
            fl.last_tracked_at AS link_last_tracked_at,
            c.id AS chat_id,
            c.notification_mode AS chat_notification_mode,
            f.id AS filter_id,
            f.key AS filter_key,
            f.value AS filter_value,
            t.id AS tag_id,
            t.name AS tag_name
        FROM filtered_links fl
            LEFT JOIN tr_chat_link cl ON cl.link_id = fl.id
            LEFT JOIN tr_chat c ON cl.chat_id = c.id
            LEFT JOIN tr_chat_link_filter clf ON clf.link_id = fl.id AND cl.chat_id = clf.chat_id
            LEFT JOIN tr_chat_link_tag clt ON clt.link_id = fl.id AND cl.chat_id = clt.chat_id
            LEFT JOIN tr_filter f ON f.id = clf.filter_id
            LEFT JOIN tr_tag t ON t.id = clt.tag_id
    """;

        return jdbcClient
                .sql(sql)
                .param(LAST_TRACKED_AT_PARAM, lastTrackedAt)
                .param(LIMIT_PARAM, limit)
                .query(new LinkRowMapper())
                .stream()
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public Link findByUrl(String url) {
        String sql =
                """
        WITH filtered_links AS (
            SELECT
                l.id,
                l.url,
                l.last_updated_at,
                l.last_tracked_at
            FROM tr_link l
            WHERE l.url = :url
        )
        SELECT
            fl.id AS link_id,
            fl.url AS link_url,
            fl.last_updated_at AS link_last_updated_at,
            fl.last_tracked_at AS link_last_tracked_at,
            c.id AS chat_id,
            c.notification_mode AS chat_notification_mode,
            f.id AS filter_id,
            f.key AS filter_key,
            f.value AS filter_value,
            t.id AS tag_id,
            t.name AS tag_name
        FROM filtered_links fl
            LEFT JOIN tr_chat_link cl ON cl.link_id = fl.id
            LEFT JOIN tr_chat c ON cl.chat_id = c.id
            LEFT JOIN tr_chat_link_filter clf ON clf.link_id = fl.id AND cl.chat_id = clf.chat_id
            LEFT JOIN tr_chat_link_tag clt ON clt.link_id = fl.id AND cl.chat_id = clt.chat_id
            LEFT JOIN tr_filter f ON f.id = clf.filter_id
            LEFT JOIN tr_tag t ON t.id = clt.tag_id
        """;

        List<Link> rows = jdbcClient.sql(sql).param(URL_PARAM, url).query(new LinkRowMapper()).stream()
                .distinct()
                .toList();

        return rows.isEmpty() ? null : rows.getFirst();
    }

    @Transactional
    public Link save(Link link) {
        Link resultLink;

        if (link.id() == null) {
            resultLink = jdbcClient
                    .sql(
                            """
                        INSERT INTO tr_link(url, last_updated_at, last_tracked_at)
                        VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, url, last_updated_at, last_tracked_at
                     """)
                    .param(URL_PARAM, link.url())
                    .param(LAST_UPDATED_AT_PARAM, link.lastUpdatedAt())
                    .param(LAST_TRACKED_AT_PARAM, link.lastTrackedAt())
                    .query(Link.class)
                    .single();
        } else {
            resultLink = jdbcClient
                    .sql(
                            """
                        UPDATE tr_link
                        SET url = :url, last_updated_at = :last_updated_at, last_tracked_at = :last_tracked_at
                        WHERE id = :id
                        RETURNING id, url, last_updated_at, last_tracked_at
                    """)
                    .param(URL_PARAM, link.url())
                    .param(LAST_UPDATED_AT_PARAM, link.lastUpdatedAt())
                    .param(LAST_TRACKED_AT_PARAM, link.lastTrackedAt())
                    .param(ID_PARAM, link.id())
                    .query(Link.class)
                    .single();
        }

        List<Long> currentChatIds = jdbcClient
                .sql("SELECT chat_id FROM tr_chat_link WHERE link_id = :link_id")
                .param(LINK_ID_PARAM, resultLink.id())
                .query(Long.class)
                .list();

        List<Chat> newChats = link.chats();

        resultLink.chats(newChats);

        List<Long> chatIdsToDelete = currentChatIds.stream()
                .filter(id -> newChats.stream().noneMatch(chat -> chat.id().equals(id)))
                .toList();

        if (!chatIdsToDelete.isEmpty()) {
            jdbcClient
                    .sql("DELETE FROM tr_chat_link_filter WHERE link_id = :link_id AND chat_id IN (:chat_ids)")
                    .param(LINK_ID_PARAM, resultLink.id())
                    .param(CHAT_IDS_PARAM, chatIdsToDelete)
                    .update();
            jdbcClient
                    .sql("DELETE FROM tr_chat_link_tag WHERE link_id = :link_id AND chat_id IN (:chat_ids)")
                    .param(LINK_ID_PARAM, resultLink.id())
                    .param(CHAT_IDS_PARAM, chatIdsToDelete)
                    .update();
            jdbcClient
                    .sql("DELETE FROM tr_chat_link WHERE link_id = :link_id AND chat_id IN (:chat_ids)")
                    .param(LINK_ID_PARAM, resultLink.id())
                    .param(CHAT_IDS_PARAM, chatIdsToDelete)
                    .update();
        }

        List<Long> chatIdsToAdd = newChats.stream()
                .map(Chat::id)
                .filter(id -> !currentChatIds.contains(id))
                .toList();

        for (Long chatId : chatIdsToAdd) {
            jdbcClient
                    .sql("INSERT INTO tr_chat(id) VALUES (:chat_id) ON CONFLICT (id) DO NOTHING")
                    .param(CHAT_ID_PARAM, chatId)
                    .update();
            jdbcClient
                    .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                    .param(CHAT_ID_PARAM, chatId)
                    .param(LINK_ID_PARAM, resultLink.id())
                    .update();
        }

        for (ChatLinkFilter chatLinkFilter : link.chatLinkFilters()) {
            Filter filter = chatLinkFilter.filter();

            Filter filterForLink = jdbcClient
                    .sql("SELECT * FROM tr_filter WHERE key = :key AND value = :value")
                    .param(FILTER_KEY_PARAM, filter.key())
                    .param(FILTER_VALUE_PARAM, filter.value())
                    .query(Filter.class)
                    .optional()
                    .orElse(null);

            if (filterForLink == null) {
                filterForLink = jdbcClient
                        .sql("INSERT INTO tr_filter(key, value) VALUES (:key, :value) RETURNING id, key, value")
                        .param(FILTER_KEY_PARAM, filter.key())
                        .param(FILTER_VALUE_PARAM, filter.value())
                        .query(Filter.class)
                        .single();
            }

            filter.id(filterForLink.id());
            chatLinkFilter.link().id(resultLink.id());

            jdbcClient
                    .sql(
                            """
                        INSERT INTO tr_chat_link_filter(chat_id, link_id, filter_id) VALUES (:chat_id, :link_id, :filter_id)
                        ON CONFLICT DO NOTHING
                    """)
                    .param(CHAT_ID_PARAM, chatLinkFilter.chat().id())
                    .param(LINK_ID_PARAM, resultLink.id())
                    .param(FILTER_ID_PARAM, filterForLink.id())
                    .update();
        }

        for (ChatLinkTag chatLinkTag : link.chatLinkTags()) {
            Tag tag = chatLinkTag.tag();

            Tag tagForLink = jdbcClient
                    .sql("SELECT * FROM tr_tag WHERE name = :name")
                    .param(TAG_NAME_PARAM, tag.name())
                    .query(Tag.class)
                    .optional()
                    .orElse(null);

            if (tagForLink == null) {
                tagForLink = jdbcClient
                        .sql("INSERT INTO tr_tag(name) VALUES (:name) RETURNING id, name")
                        .param(TAG_NAME_PARAM, tag.name())
                        .query(Tag.class)
                        .single();
            }

            tag.id(tagForLink.id());
            chatLinkTag.link().id(resultLink.id());

            jdbcClient
                    .sql(
                            """
                         INSERT INTO tr_chat_link_tag(chat_id, link_id, tag_id) VALUES (:chat_id, :link_id, :tag_id)
                         ON CONFLICT DO NOTHING
                     """)
                    .param(CHAT_ID_PARAM, chatLinkTag.chat().id())
                    .param(LINK_ID_PARAM, resultLink.id())
                    .param(TAG_ID_PARAM, tagForLink.id())
                    .update();
        }

        resultLink.chatLinkTags(link.chatLinkTags());
        resultLink.chatLinkFilters(link.chatLinkFilters());

        return resultLink;
    }

    @Transactional
    public void removeOrphanedLinks() {
        jdbcClient
                .sql(
                        """
        DELETE FROM tr_link l
        WHERE NOT EXISTS (
            SELECT 1
            FROM tr_chat_link_filter clf
            WHERE clf.link_id = l.id
        )
        AND NOT EXISTS (
            SELECT 1
            FROM tr_chat_link_tag clt
            WHERE clt.link_id = l.id
        )
        AND NOT EXISTS (
            SELECT 1
            FROM tr_chat_link cl
            WHERE cl.link_id = l.id
        )
        """)
                .update();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAfterTracking(String url, Long lastUpdatedAt, Long lastTrackedAt) {
        jdbcClient
                .sql(
                        """
            UPDATE tr_link
            SET
                last_updated_at = COALESCE(:last_updated_at, last_updated_at),
                last_tracked_at = COALESCE(:last_tracked_at, last_tracked_at)
            WHERE url = :url
        """)
                .param(LAST_UPDATED_AT_PARAM, lastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, lastTrackedAt)
                .param(URL_PARAM, url)
                .update();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getGithubLinksCount() {
        return jdbcClient
                .sql("""
            SELECT count(*) from tr_link l where url like '%github%'
            """)
                .query(Long.class)
                .single();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getStackoverflowLinksCount() {
        return jdbcClient
                .sql("""
            SELECT count(*) from tr_link l where url like '%stackoverflow%'
            """)
                .query(Long.class)
                .single();
    }
}
