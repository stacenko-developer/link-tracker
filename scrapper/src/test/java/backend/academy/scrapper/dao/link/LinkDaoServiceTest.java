package backend.academy.scrapper.dao.link;

import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_KEY_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_VALUE_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_TRACKED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LAST_UPDATED_AT_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LINK_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_NAME_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.URL_PARAM;

import backend.academy.scrapper.TestConfiguration;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.JpaLinkDaoService;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.enums.NotificationMode;
import jakarta.persistence.EntityManager;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public abstract class LinkDaoServiceTest extends TestConfiguration {

    private static final URI DEFAULT_URL = URI.create("https://github.com/stacenko-developer");
    private static final Long DEFAULT_LAST_UPDATED_AT = 1L;
    private static final Long DEFAULT_LAST_TRACKED_AT = 2L;
    private static final Long DEFAULT_CHAT_ID = 1L;
    private static final NotificationMode DEFAULT_NOTIFICATION_MODE = NotificationMode.IMMEDIATE;

    @Autowired
    protected LinkDaoService linkDaoService;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    public void findByUrlAndChatIdWithCorrectArguments_ShouldFindLink() {
        Long firstChatId = 1L;
        Long secondChatId = 2L;
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();

        int expectedLinksCount = 1;
        int expectedChatsCount = 2;

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, firstChatId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, secondChatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, linkId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, linkId)
                .update();

        Link foundLink = linkDaoService.findByUrlAndChatId(DEFAULT_URL, firstChatId);
        Chat firstChat = foundLink.chats().getFirst();
        Chat secondChat = foundLink.chats().getLast();

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(linkId, foundLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertTrue(
                foundLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                foundLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                firstChat.links().stream().anyMatch(link -> link.id().equals(linkId)));
        Assertions.assertTrue(
                secondChat.links().stream().anyMatch(link -> link.id().equals(linkId)));
        Assertions.assertEquals(expectedChatsCount, foundLink.chats().size());
        Assertions.assertEquals(expectedLinksCount, firstChat.links().size());
    }

    @Test
    @Rollback
    @Transactional
    public void findByUrlAndChatIdWithNotAddedChatId_ShouldReturnNull() {
        jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at)")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .update();

        Link foundLink = linkDaoService.findByUrlAndChatId(DEFAULT_URL, DEFAULT_CHAT_ID);

        Assertions.assertNull(foundLink);
    }

    @Test
    @Rollback
    @Transactional
    public void findByUrlAndChatIdWithNotAddedLink_ShouldReturnNull() {
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();
        Long chatId = jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id) RETURNING id")
                .param(ID_PARAM, DEFAULT_CHAT_ID)
                .query(Long.class)
                .single();
        URI incorrectUrl = URI.create("https://github.com/stacenko-developer/incorrect");

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, linkId)
                .update();

        Link foundLink = linkDaoService.findByUrlAndChatId(incorrectUrl, DEFAULT_CHAT_ID);

        Assertions.assertNull(foundLink);
    }

    @Test
    @Rollback
    @Transactional
    public void findByUrlWithCorrectArgumentsAndAddedChat_ShouldFindLink() {
        Long firstChatId = 1L;
        Long secondChatId = 2L;
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();

        int expectedLinksCount = 1;
        int expectedChatsCount = 2;

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, firstChatId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, secondChatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, linkId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, linkId)
                .update();

        Link foundLink = linkDaoService.findByUrl(DEFAULT_URL);
        Chat firstChat = foundLink.chats().getFirst();
        Chat secondChat = foundLink.chats().getLast();

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(linkId, foundLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertTrue(
                foundLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                foundLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                firstChat.links().stream().anyMatch(link -> link.id().equals(linkId)));
        Assertions.assertTrue(
                secondChat.links().stream().anyMatch(link -> link.id().equals(linkId)));
        Assertions.assertEquals(expectedChatsCount, foundLink.chats().size());
        Assertions.assertEquals(expectedLinksCount, firstChat.links().size());
    }

    @Test
    @Rollback
    @Transactional
    public void findByUrlWithCorrectArgumentsAndNotAddedChat_ShouldFindLink() {
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();

        Link foundLink = linkDaoService.findByUrl(DEFAULT_URL);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(linkId, foundLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());
        Assertions.assertTrue(foundLink.chats().isEmpty());
        Assertions.assertTrue(foundLink.chatLinkFilters().isEmpty());
        Assertions.assertTrue(foundLink.chatLinkTags().isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void findByUrlWithNotAddedLink_ShouldReturnNull() {
        URI incorrectUrl = URI.create("https://github.com/stacenko-developer/incorrect");

        jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at)")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .update();

        Link foundLink = linkDaoService.findByUrl(incorrectUrl);

        Assertions.assertNull(foundLink);
    }

    @Test
    @Rollback
    @Transactional
    public void getAllLinksWithCorrectArguments_ShouldReturnAllOldLinksForTracking() {
        String firstUrl = "https://first-link.ru";
        Long firstLastUpdatedAt = 1L;
        Long firstLastTrackedAt = 2L;

        String secondUrl = "https://second-link.ru";
        Long secondLastUpdatedAt = 3L;
        Long secondLastTrackedAt = 4L;

        Long firstChatId = 1L;
        Long secondChatId = 2L;

        Long lastTrackedAtBefore = 5L;
        Integer limit = 2;

        long expectedLinksCount = 2;

        Link firstLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id,last_updated_at, last_tracked_at")
                .param(URL_PARAM, firstUrl)
                .param(LAST_UPDATED_AT_PARAM, firstLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, firstLastTrackedAt)
                .query(Link.class)
                .single();
        Link secondLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id,last_updated_at, last_tracked_at")
                .param(URL_PARAM, secondUrl)
                .param(LAST_UPDATED_AT_PARAM, secondLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, secondLastTrackedAt)
                .query(Link.class)
                .single();

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, firstChatId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, secondChatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();

        List<Link> links = linkDaoService.getAllOldLinks(lastTrackedAtBefore, limit);

        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinksCount, links.size());

        Link firstAddedLink = links.stream()
                .filter(link -> link.id().equals(firstLink.id()))
                .findFirst()
                .orElse(null);
        Link secondAddedLink = links.stream()
                .filter(link -> link.id().equals(secondLink.id()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(firstAddedLink);
        Assertions.assertNotNull(secondAddedLink);

        Chat firstAddedLinkChat = firstAddedLink.chats().getFirst();
        Chat secondAddedLinkChat = secondAddedLink.chats().getFirst();

        Assertions.assertEquals(firstUrl, firstAddedLink.url());
        Assertions.assertEquals(firstLastUpdatedAt, firstAddedLink.lastUpdatedAt());
        Assertions.assertEquals(firstLastTrackedAt, firstAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                firstAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(firstAddedLink.id())));
        Assertions.assertTrue(
                firstAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(secondAddedLink.id())));

        Assertions.assertEquals(secondUrl, secondAddedLink.url());
        Assertions.assertEquals(secondLastUpdatedAt, secondAddedLink.lastUpdatedAt());
        Assertions.assertEquals(secondLastTrackedAt, secondAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                secondAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                secondAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                secondAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(firstAddedLink.id())));
        Assertions.assertTrue(
                secondAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(secondAddedLink.id())));
    }

    @Test
    @Rollback
    @Transactional
    public void getAllLinksWithCorrectArguments_ShouldReturnCorrectOldLinksForTrackingByFilter() {
        String firstUrl = "https://first-link.ru";
        Long firstLastUpdatedAt = 1L;
        Long firstLastTrackedAt = 2L;

        String secondUrl = "https://second-link.ru";
        Long secondLastUpdatedAt = 3L;
        Long secondLastTrackedAt = 4L;

        Long firstChatId = 1L;
        Long secondChatId = 2L;

        Long lastTrackedAtBefore = 3L;
        Integer limit = 2;

        long expectedLinksCount = 1;

        Link firstLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, firstUrl)
                .param(LAST_UPDATED_AT_PARAM, firstLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, firstLastTrackedAt)
                .query(Link.class)
                .single();
        Link secondLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, secondUrl)
                .param(LAST_UPDATED_AT_PARAM, secondLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, secondLastTrackedAt)
                .query(Link.class)
                .single();

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, firstChatId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, secondChatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();

        List<Link> links = linkDaoService.getAllOldLinks(lastTrackedAtBefore, limit);

        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinksCount, links.size());

        Link firstAddedLink = links.stream()
                .filter(link -> link.id().equals(firstLink.id()))
                .findFirst()
                .orElse(null);
        Link secondAddedLink = links.stream()
                .filter(link -> link.id().equals(secondLink.id()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(firstAddedLink);
        Assertions.assertNull(secondAddedLink);

        Chat firstAddedLinkChat = firstAddedLink.chats().getFirst();

        Assertions.assertEquals(firstUrl, firstAddedLink.url());
        Assertions.assertEquals(firstLastUpdatedAt, firstAddedLink.lastUpdatedAt());
        Assertions.assertEquals(firstLastTrackedAt, firstAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                firstAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(firstAddedLink.id())));
    }

    @Test
    @Rollback
    @Transactional
    public void getAllLinksWithCorrectArguments_ShouldReturnCorrectOldLinksForTrackingByLimit() {
        String firstUrl = "https://first-link.ru";
        Long firstLastUpdatedAt = 1L;
        Long firstLastTrackedAt = 2L;

        String secondUrl = "https://second-link.ru";
        Long secondLastUpdatedAt = 3L;
        Long secondLastTrackedAt = 4L;

        Long firstChatId = 1L;
        Long secondChatId = 2L;

        Long lastTrackedAtBefore = 5L;
        Integer limit = 1;

        long expectedLinksCount = 1;

        Link firstLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, firstUrl)
                .param(LAST_UPDATED_AT_PARAM, firstLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, firstLastTrackedAt)
                .query(Link.class)
                .single();
        Link secondLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, secondUrl)
                .param(LAST_UPDATED_AT_PARAM, secondLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, secondLastTrackedAt)
                .query(Link.class)
                .single();

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, firstChatId)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, secondChatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, firstChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, secondChatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();

        List<Link> links = linkDaoService.getAllOldLinks(lastTrackedAtBefore, limit);

        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinksCount, links.size());

        Link firstAddedLink = links.stream()
                .filter(link -> link.id().equals(firstLink.id()))
                .findFirst()
                .orElse(null);
        Link secondAddedLink = links.stream()
                .filter(link -> link.id().equals(secondLink.id()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(firstAddedLink);
        Assertions.assertNull(secondAddedLink);

        Chat firstAddedLinkChat = firstAddedLink.chats().getFirst();

        Assertions.assertEquals(firstUrl, firstAddedLink.url());
        Assertions.assertEquals(firstLastUpdatedAt, firstAddedLink.lastUpdatedAt());
        Assertions.assertEquals(firstLastTrackedAt, firstAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(firstChatId)));
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(secondChatId)));
        Assertions.assertTrue(
                firstAddedLinkChat.links().stream().anyMatch(link -> link.id().equals(firstAddedLink.id())));
    }

    @Test
    @Rollback
    @Transactional
    public void getAllLinksByChatIdWithCorrectArguments_ShouldReturnOldUserTrackedUserTrackingLinksForTracking() {
        String firstUrl = "https://first-link.ru";
        Long firstLastUpdatedAt = 1L;
        Long firstLastTrackedAt = 2L;

        String secondUrl = "https://second-link.ru";
        Long secondLastUpdatedAt = 3L;
        Long secondLastTrackedAt = 4L;

        Long chatId = 1L;

        long expectedLinksCount = 2;

        Link firstLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, firstUrl)
                .param(LAST_UPDATED_AT_PARAM, firstLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, firstLastTrackedAt)
                .query(Link.class)
                .single();
        Link secondLink = jdbcClient
                .sql(
                        "INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                                + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING id, last_updated_at, last_tracked_at")
                .param(URL_PARAM, secondUrl)
                .param(LAST_UPDATED_AT_PARAM, secondLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, secondLastTrackedAt)
                .query(Link.class)
                .single();

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param(ID_PARAM, chatId)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, firstLink.id())
                .update();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, secondLink.id())
                .update();

        List<Link> links = linkDaoService.getAllUserTrackingLinks(chatId, null);

        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinksCount, links.size());

        Link firstAddedLink = links.stream()
                .filter(link -> link.id().equals(firstLink.id()))
                .findFirst()
                .orElse(null);
        Link secondAddedLink = links.stream()
                .filter(link -> link.id().equals(secondLink.id()))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(firstAddedLink);
        Assertions.assertNotNull(secondAddedLink);

        Chat resultChat = firstAddedLink.chats().getFirst();

        Assertions.assertEquals(firstUrl, firstAddedLink.url());
        Assertions.assertEquals(firstLastUpdatedAt, firstAddedLink.lastUpdatedAt());
        Assertions.assertEquals(firstLastTrackedAt, firstAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                firstAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(chatId)));

        Assertions.assertEquals(secondUrl, secondAddedLink.url());
        Assertions.assertEquals(secondLastUpdatedAt, secondAddedLink.lastUpdatedAt());
        Assertions.assertEquals(secondLastTrackedAt, secondAddedLink.lastTrackedAt());
        Assertions.assertTrue(
                secondAddedLink.chats().stream().anyMatch(chat -> chat.id().equals(chatId)));

        Assertions.assertTrue(
                resultChat.links().stream().anyMatch(link -> link.id().equals(firstAddedLink.id())));
        Assertions.assertTrue(
                resultChat.links().stream().anyMatch(link -> link.id().equals(secondAddedLink.id())));
    }

    @Test
    @Rollback
    @Transactional
    public void getAllOldLinksForTrackingByChatIdWithCorrectArgumentsForNewChat_ShouldReturnEmptyList() {
        String firstUrl = "https://first-link.ru";
        Long firstLastUpdatedAt = 1L;
        Long firstLastTrackedAt = 2L;

        String secondUrl = "https://second-link.ru";
        Long secondLastUpdatedAt = 3L;
        Long secondLastTrackedAt = 4L;

        Long chatId = 1L;

        jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at)")
                .param(URL_PARAM, firstUrl)
                .param(LAST_UPDATED_AT_PARAM, firstLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, firstLastTrackedAt)
                .update();
        jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at)")
                .param(URL_PARAM, secondUrl)
                .param(LAST_UPDATED_AT_PARAM, secondLastUpdatedAt)
                .param(LAST_TRACKED_AT_PARAM, secondLastTrackedAt)
                .update();

        jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id)")
                .param("id", chatId)
                .update();

        List<Link> links = linkDaoService.getAllUserTrackingLinks(chatId, null);

        Assertions.assertTrue(links.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void removeOrphanedLinks_ShouldRemoveOrphanedLinks() {
        jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at)")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .update();

        linkDaoService.removeOrphanedLinks();

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Assertions.assertNull(foundLink);
    }

    @Test
    @Rollback
    @Transactional
    public void removeNotOrphanedLinks_ShouldNotLinks() {
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING ID")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();
        Long chatId = jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id) RETURNING ID")
                .param(ID_PARAM, DEFAULT_CHAT_ID)
                .query(Long.class)
                .single();

        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, linkId)
                .update();

        linkDaoService.removeOrphanedLinks();

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());
    }

    @Test
    @Rollback
    @Transactional
    public void createLinkWithoutChats_ShouldCorrectlyCreate() {
        Link link = new Link();
        link.url(DEFAULT_URL.toString());
        link.lastUpdatedAt(DEFAULT_LAST_UPDATED_AT);
        link.lastTrackedAt(DEFAULT_LAST_TRACKED_AT);

        Link createdLink = linkDaoService.save(link);

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertNotNull(createdLink);
        Assertions.assertEquals(foundLink.id(), createdLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), createdLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, createdLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, createdLink.lastTrackedAt());
    }

    @Test
    @Rollback
    @Transactional
    public void updateLinkWithoutChats_ShouldCorrectlyUpdate() {
        String newUrl = "https://new-url.com";
        Long newLastUpdatedAt = 10L;
        Long newLastTrackedAt = 20L;
        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING ID")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();

        Link link = new Link();
        link.id(linkId);
        link.url(newUrl);
        link.lastUpdatedAt(newLastUpdatedAt);
        link.lastTrackedAt(newLastTrackedAt);

        Link updatedLink = linkDaoService.save(link);

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, newUrl)
                .query(Link.class)
                .optional()
                .orElse(null);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(newUrl, foundLink.url());
        Assertions.assertEquals(newLastUpdatedAt, foundLink.lastUpdatedAt());
        Assertions.assertEquals(newLastTrackedAt, foundLink.lastTrackedAt());

        Assertions.assertNotNull(updatedLink);
        Assertions.assertEquals(foundLink.id(), updatedLink.id());
        Assertions.assertEquals(newUrl, updatedLink.url());
        Assertions.assertEquals(newLastUpdatedAt, updatedLink.lastUpdatedAt());
        Assertions.assertEquals(newLastTrackedAt, updatedLink.lastTrackedAt());
    }

    @Test
    @Rollback
    @Transactional
    public void createLinkWithChat_ShouldCorrectlyCreate() {
        int correctChatsCount = 1;

        Link link = new Link();
        link.url(DEFAULT_URL.toString());
        link.lastUpdatedAt(DEFAULT_LAST_UPDATED_AT);
        link.lastTrackedAt(DEFAULT_LAST_TRACKED_AT);

        Chat chatToAdd = new Chat();
        chatToAdd.id(DEFAULT_CHAT_ID);
        chatToAdd.notificationMode(DEFAULT_NOTIFICATION_MODE);

        chatToAdd.links().add(link);
        link.chats().add(chatToAdd);

        Link createdLink = linkDaoService.save(link);

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Boolean isChatAdded = jdbcClient
                .sql(
                        """
                SELECT EXISTS (
                    SELECT 1 FROM tr_chat_link WHERE chat_id = :chat_id AND link_id = :link_id
                )
            """)
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, createdLink.id())
                .query(Boolean.class)
                .single();

        Assertions.assertTrue(isChatAdded);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertNotNull(createdLink);
        Assertions.assertEquals(foundLink.id(), createdLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), createdLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, createdLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, createdLink.lastTrackedAt());

        List<Chat> resultChats = createdLink.chats();

        Assertions.assertNotNull(resultChats);
        Assertions.assertEquals(correctChatsCount, resultChats.size());
        Assertions.assertEquals(chatToAdd.id(), resultChats.getFirst().id());
    }

    @Test
    @Rollback
    @Transactional
    public void createLinkWithChatAndTag_ShouldCorrectlyCreate() {
        int correctChatsCount = 1;
        String defaultTagName = "name";

        Link link = new Link();
        link.url(DEFAULT_URL.toString());
        link.lastUpdatedAt(DEFAULT_LAST_UPDATED_AT);
        link.lastTrackedAt(DEFAULT_LAST_TRACKED_AT);

        Chat chatToAdd = new Chat();
        chatToAdd.id(DEFAULT_CHAT_ID);
        chatToAdd.notificationMode(DEFAULT_NOTIFICATION_MODE);

        chatToAdd.links().add(link);
        link.chats().add(chatToAdd);

        Tag tag = new Tag();
        tag.name(defaultTagName);

        ChatLinkTag chatLinkTag = new ChatLinkTag();

        chatLinkTag.tag(tag);
        chatLinkTag.chat(chatToAdd);
        chatLinkTag.link(link);

        link.chatLinkTags().add(chatLinkTag);

        Link createdLink = linkDaoService.save(link);

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Boolean isChatAdded = jdbcClient
                .sql(
                        """
                SELECT EXISTS (
                    SELECT 1 FROM tr_chat_link WHERE chat_id = :chat_id AND link_id = :link_id
                )
            """)
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, createdLink.id())
                .query(Boolean.class)
                .single();
        Boolean isTagAdded = jdbcClient
                .sql(
                        """
                SELECT EXISTS (
                    SELECT 1 FROM tr_chat_link_tag clt
                        JOIN tr_tag t ON clt.tag_id = t.id
                    WHERE chat_id = :chat_id AND link_id = :link_id AND t.name = :name
                )
            """)
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, createdLink.id())
                .param(TAG_NAME_PARAM, tag.name())
                .query(Boolean.class)
                .single();

        Assertions.assertTrue(isChatAdded);
        Assertions.assertTrue(isTagAdded);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertNotNull(createdLink);
        Assertions.assertEquals(foundLink.id(), createdLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), createdLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, createdLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, createdLink.lastTrackedAt());

        List<Chat> resultChats = createdLink.chats();

        Assertions.assertNotNull(resultChats);
        Assertions.assertEquals(correctChatsCount, resultChats.size());
        Assertions.assertEquals(chatToAdd.id(), resultChats.getFirst().id());

        ChatLinkTag resultChatLinkTag = createdLink.chatLinkTags().getFirst();

        Assertions.assertEquals(DEFAULT_CHAT_ID, resultChatLinkTag.chat().id());
        Assertions.assertEquals(createdLink.id(), resultChatLinkTag.link().id());
        Assertions.assertEquals(tag.name(), resultChatLinkTag.tag().name());
    }

    @Test
    @Rollback
    @Transactional
    public void createLinkWithChatAndFilter_ShouldCorrectlyCreate() {
        int correctChatsCount = 1;
        String defaultFilterKey = "key";
        String defaultFilterValue = "value";

        Link link = new Link();
        link.url(DEFAULT_URL.toString());
        link.lastUpdatedAt(DEFAULT_LAST_UPDATED_AT);
        link.lastTrackedAt(DEFAULT_LAST_TRACKED_AT);

        Chat chatToAdd = new Chat();
        chatToAdd.id(DEFAULT_CHAT_ID);
        chatToAdd.notificationMode(DEFAULT_NOTIFICATION_MODE);

        chatToAdd.links().add(link);
        link.chats().add(chatToAdd);

        Filter filter = new Filter();
        filter.key(defaultFilterKey);
        filter.value(defaultFilterValue);

        ChatLinkFilter chatLinkFilter = new ChatLinkFilter();

        chatLinkFilter.filter(filter);
        chatLinkFilter.chat(chatToAdd);
        chatLinkFilter.link(link);

        link.chatLinkFilters().add(chatLinkFilter);

        Link createdLink = linkDaoService.save(link);

        if (linkDaoService instanceof JpaLinkDaoService) {
            entityManager.flush();
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Boolean isChatAdded = jdbcClient
                .sql(
                        """
                SELECT EXISTS (
                    SELECT 1 FROM tr_chat_link WHERE chat_id = :chat_id AND link_id = :link_id
                )
            """)
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, createdLink.id())
                .query(Boolean.class)
                .single();
        Boolean isFilterAdded = jdbcClient
                .sql(
                        """
                SELECT EXISTS (
                    SELECT 1 FROM tr_chat_link_filter clf
                        JOIN tr_filter f ON clf.filter_id = f.id
                    WHERE chat_id = :chat_id AND link_id = :link_id AND f.key = :key AND f.value = :value
                )
            """)
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, createdLink.id())
                .param(FILTER_KEY_PARAM, filter.key())
                .param(FILTER_VALUE_PARAM, filter.value())
                .query(Boolean.class)
                .single();

        Assertions.assertTrue(isChatAdded);
        Assertions.assertTrue(isFilterAdded);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertNotNull(createdLink);
        Assertions.assertEquals(foundLink.id(), createdLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), createdLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, createdLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, createdLink.lastTrackedAt());

        List<Chat> resultChats = createdLink.chats();

        Assertions.assertNotNull(resultChats);
        Assertions.assertEquals(correctChatsCount, resultChats.size());
        Assertions.assertEquals(chatToAdd.id(), resultChats.getFirst().id());

        ChatLinkFilter resultChatLinkFilter = createdLink.chatLinkFilters().getFirst();

        Assertions.assertEquals(DEFAULT_CHAT_ID, resultChatLinkFilter.chat().id());
        Assertions.assertEquals(createdLink.id(), resultChatLinkFilter.link().id());
        Assertions.assertEquals(filter.key(), resultChatLinkFilter.filter().key());
        Assertions.assertEquals(filter.value(), resultChatLinkFilter.filter().value());
    }

    @Test
    @Rollback
    @Transactional
    public void removeChatFromLinkWithCorrectArguments_ShouldCorrectlyRemove() {
        int correctChatsCount = 0;

        Long linkId = jdbcClient
                .sql("INSERT INTO tr_link(url, last_updated_at, last_tracked_at) "
                        + "VALUES (:url, :last_updated_at, :last_tracked_at) RETURNING ID")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .param(LAST_UPDATED_AT_PARAM, DEFAULT_LAST_UPDATED_AT)
                .param(LAST_TRACKED_AT_PARAM, DEFAULT_LAST_TRACKED_AT)
                .query(Long.class)
                .single();
        Long chatId = jdbcClient
                .sql("INSERT INTO tr_chat(id) VALUES (:id) RETURNING ID")
                .param(ID_PARAM, DEFAULT_CHAT_ID)
                .query(Long.class)
                .single();
        jdbcClient
                .sql("INSERT INTO tr_chat_link(chat_id, link_id) VALUES (:chat_id, :link_id)")
                .param(CHAT_ID_PARAM, chatId)
                .param(LINK_ID_PARAM, linkId)
                .update();

        Link link = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Link updatedLink;

        if (linkDaoService instanceof JpaLinkDaoService) {
            Link linkToDelete = linkDaoService.findByUrl(DEFAULT_URL);

            Chat chat = linkToDelete.chats().getFirst();
            chat.links().removeIf(l -> l.id().equals(linkToDelete.id()));
            linkToDelete.chats().removeIf(ch -> ch.id().equals(chat.id()));

            updatedLink = linkDaoService.save(link);

            entityManager.flush();
        } else {
            updatedLink = linkDaoService.save(link);
        }

        Link foundLink = jdbcClient
                .sql("SELECT * FROM tr_link WHERE url = :url")
                .param(URL_PARAM, DEFAULT_URL.toString())
                .query(Link.class)
                .optional()
                .orElse(null);

        Chat foundChat = jdbcClient
                .sql("SELECT * FROM tr_chat_link WHERE chat_id = :chat_id AND link_id = :link_id")
                .param(CHAT_ID_PARAM, DEFAULT_CHAT_ID)
                .param(LINK_ID_PARAM, linkId)
                .query(Chat.class)
                .optional()
                .orElse(null);

        Assertions.assertNull(foundChat);

        Assertions.assertNotNull(foundLink);
        Assertions.assertEquals(DEFAULT_URL.toString(), foundLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, foundLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, foundLink.lastTrackedAt());

        Assertions.assertNotNull(updatedLink);
        Assertions.assertEquals(foundLink.id(), updatedLink.id());
        Assertions.assertEquals(DEFAULT_URL.toString(), updatedLink.url());
        Assertions.assertEquals(DEFAULT_LAST_UPDATED_AT, updatedLink.lastUpdatedAt());
        Assertions.assertEquals(DEFAULT_LAST_TRACKED_AT, updatedLink.lastTrackedAt());

        List<Chat> resultChats = updatedLink.chats();

        Assertions.assertNotNull(resultChats);
        Assertions.assertEquals(correctChatsCount, resultChats.size());
    }
}
