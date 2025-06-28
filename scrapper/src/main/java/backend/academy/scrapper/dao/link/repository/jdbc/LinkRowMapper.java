package backend.academy.scrapper.dao.link.repository.jdbc;

import static backend.academy.scrapper.constants.DatabaseConstants.CHAT_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.FILTER_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.LINK_ID_PARAM;
import static backend.academy.scrapper.constants.DatabaseConstants.TAG_ID_PARAM;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilterId;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTagId;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.enums.NotificationMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

public class LinkRowMapper implements RowMapper<Link> {

    private static final String LINK_URL_PARAM = "link_url";
    private static final String LINK_LAST_UPDATED_AT = "link_last_updated_at";
    private static final String LINK_LAST_TRACKED_AT = "link_last_tracked_at";
    private static final String CHAT_NOTIFICATION_MODE_PARAM = "chat_notification_mode";

    private static final String FILTER_KEY_PARAM = "filter_key";
    private static final String FILTER_VALUE_PARAM = "filter_value";
    private static final String TAG_NAME_PARAM = "tag_name";

    private final Map<Long, Link> links = new HashMap<>();
    private final Map<Long, Chat> chats = new HashMap<>();
    private final Map<Long, Filter> filters = new HashMap<>();
    private final Map<Long, Tag> tags = new HashMap<>();
    private final Map<ChatLinkFilterId, ChatLinkFilter> chatLinkFilters = new HashMap<>();
    private final Map<ChatLinkTagId, ChatLinkTag> chatLinkTags = new HashMap<>();

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long linkId = rs.getLong(LINK_ID_PARAM);

        Link link = links.computeIfAbsent(linkId, id -> {
            Link newLink = new Link();
            newLink.id(id);

            try {
                String url = rs.getString(LINK_URL_PARAM);

                if (!rs.wasNull()) {
                    newLink.url(url);
                }

                Long lastUpdatedAt = rs.getLong(LINK_LAST_UPDATED_AT);

                if (!rs.wasNull()) {
                    newLink.lastUpdatedAt(lastUpdatedAt);
                }

                Long lastTrackedAt = rs.getLong(LINK_LAST_TRACKED_AT);

                if (!rs.wasNull()) {
                    newLink.lastTrackedAt(lastTrackedAt);
                }
            } catch (SQLException sqlException) {
                return newLink;
            }

            return newLink;
        });

        Long chatId = rs.getLong(CHAT_ID_PARAM);

        if (rs.wasNull()) {
            return link;
        }

        Chat chat = chats.computeIfAbsent(chatId, id -> {
            Chat newChat = new Chat();

            try {
                String notificationMode = rs.getString(CHAT_NOTIFICATION_MODE_PARAM);

                newChat.id(id);
                newChat.notificationMode(NotificationMode.valueOf(notificationMode));
                return newChat;
            } catch (SQLException e) {
                return newChat;
            }
        });

        if (!chat.links().contains(link)) {
            chat.links().add(link);
        }

        processFilter(rs, chat, link);
        processTag(rs, chat, link);

        link.chats(new ArrayList<>(chats.values()));
        link.chatLinkFilters(new ArrayList<>(chatLinkFilters.values()));
        link.chatLinkTags(new ArrayList<>(chatLinkTags.values()));

        return link;
    }

    private void processFilter(ResultSet rs, Chat chat, Link link) throws SQLException {
        Long filterId = rs.getLong(FILTER_ID_PARAM);

        if (rs.wasNull()) {
            return;
        }

        Filter filter = filters.computeIfAbsent(filterId, id -> {
            Filter newFilter = new Filter();

            try {
                newFilter.id(id);
                newFilter.key(rs.getString(FILTER_KEY_PARAM));
                newFilter.value(rs.getString(FILTER_VALUE_PARAM));
                return newFilter;
            } catch (SQLException e) {
                return newFilter;
            }
        });

        ChatLinkFilterId chatLinkFilterId = new ChatLinkFilterId(chat.id(), link.id(), filterId);
        ChatLinkFilter chatLinkFilter = chatLinkFilters.computeIfAbsent(chatLinkFilterId, id -> {
            ChatLinkFilter newChatLinkFilter = new ChatLinkFilter();
            newChatLinkFilter.id(id);
            newChatLinkFilter.chat(chat);
            newChatLinkFilter.filter(filter);
            newChatLinkFilter.link(link);
            return newChatLinkFilter;
        });

        filter.chatLinkFilters().add(chatLinkFilter);
        chat.chatLinkFilters().add(chatLinkFilter);
    }

    private void processTag(ResultSet rs, Chat chat, Link link) throws SQLException {
        Long tagId = rs.getLong(TAG_ID_PARAM);

        if (rs.wasNull()) {
            return;
        }

        Tag tag = tags.computeIfAbsent(tagId, id -> {
            Tag newTag = new Tag();

            try {
                newTag.id(id);
                newTag.name(rs.getString(TAG_NAME_PARAM));
                return newTag;
            } catch (SQLException e) {
                return newTag;
            }
        });

        ChatLinkTagId chatLinkTagId = new ChatLinkTagId(chat.id(), link.id(), tagId);
        ChatLinkTag chatLinkTag = chatLinkTags.computeIfAbsent(chatLinkTagId, id -> {
            ChatLinkTag newChatLinkTag = new ChatLinkTag();
            newChatLinkTag.id(id);
            newChatLinkTag.chat(chat);
            newChatLinkTag.tag(tag);
            newChatLinkTag.link(link);
            return newChatLinkTag;
        });

        tag.chatLinkTags().add(chatLinkTag);
        chat.chatLinkTags().add(chatLinkTag);
    }
}
