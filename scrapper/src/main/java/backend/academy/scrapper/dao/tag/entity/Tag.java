package backend.academy.scrapper.dao.tag.entity;

import backend.academy.common.dao.BaseEntity;
import backend.academy.scrapper.dao.chatLinkTag.entity.ChatLinkTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Сущность пользовательского тэга для отслеживаемой ссылки.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи один-ко-многим с ChatLinkTag
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_tag")
public class Tag extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -5002336710323054332L;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();
}
