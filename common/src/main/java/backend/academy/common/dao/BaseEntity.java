package backend.academy.common.dao;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Базовый класс для всех сущностей с автоинкрементным ID.
 *
 * <p>Используются явные @Getter/@Setter вместо @Data из-за:
 *
 * <ul>
 *   <li>Предотвращения конфликтов при расширении класса дочерними сущностями
 * </ul>
 */
@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 334119101131958326L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
