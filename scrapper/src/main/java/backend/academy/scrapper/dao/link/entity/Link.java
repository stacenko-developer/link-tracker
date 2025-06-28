package backend.academy.scrapper.dao.link.entity;

import backend.academy.common.dao.BaseEntity;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность ссылки для отслеживания.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи многие-ко-многим с Chat
 *   <li>Связи один-ко-многим с ChatLinkFilter
 *   <li>Связи один-ко-многим с ChatLinkTag
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_link")
public class Link extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5837516069143070306L;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(nullable = false)
    private Long lastUpdatedAt;

    @Column
    private Long lastTrackedAt;

    @ManyToMany(mappedBy = "links", cascade = CascadeType.PERSIST)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkFilter> chatLinkFilters = new ArrayList<>();

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();
}
