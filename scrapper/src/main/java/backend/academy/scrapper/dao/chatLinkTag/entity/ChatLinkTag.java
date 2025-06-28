package backend.academy.scrapper.dao.chatLinkTag.entity;

import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.tag.entity.Tag;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность для связи чата, ссылки и тэга.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи многие к одному с Chat
 *   <li>Связи многие к одному с Link
 *   <li>Связи многие к одному с Tag
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_chat_link_tag")
public class ChatLinkTag {
    @EmbeddedId
    private ChatLinkTagId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @MapsId("linkId")
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
