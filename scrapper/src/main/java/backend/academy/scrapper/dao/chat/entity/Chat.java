package backend.academy.scrapper.dao.chat.entity;

import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.enums.NotificationMode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность пользовательского чата.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи один-ко-многим с ChatLinkFilter
 *   <li>Связи один-ко-многим с ChatLinkTag
 *   <li>Связи многие-ко-многим с Link
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_chat")
public class Chat {
    @Id
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationMode notificationMode;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkFilter> chatLinkFilters = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tr_chat_link",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "link_id"))
    private List<Link> links = new ArrayList<>();
}
