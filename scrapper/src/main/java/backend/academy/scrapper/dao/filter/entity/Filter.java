package backend.academy.scrapper.dao.filter.entity;

import backend.academy.common.dao.BaseEntity;
import backend.academy.scrapper.dao.chatLinkFilter.entity.ChatLinkFilter;
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
 * Сущность пользовательского фильтра для отслеживаемой ссылки.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Связи один-ко-многим с ChatLinkFilter
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "tr_filter")
public class Filter extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6329395326150186275L;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkFilter> chatLinkFilters = new ArrayList<>();
}
