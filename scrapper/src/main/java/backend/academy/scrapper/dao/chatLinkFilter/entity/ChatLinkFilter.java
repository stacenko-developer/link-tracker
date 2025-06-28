package backend.academy.scrapper.dao.chatLinkFilter.entity;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.link.entity.Link;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность для связи чата, ссылки и фильтра.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи многие к одному с Chat
 *   <li>Связи многие к одному с Link
 *   <li>Связи многие к одному с Filter
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_chat_link_filter")
public class ChatLinkFilter {
    @EmbeddedId
    private ChatLinkFilterId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @MapsId("linkId")
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    @ManyToOne
    @MapsId("filterId")
    @JoinColumn(name = "filter_id", nullable = false)
    private Filter filter;
}
