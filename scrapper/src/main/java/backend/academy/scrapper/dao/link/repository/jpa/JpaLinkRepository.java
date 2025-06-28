package backend.academy.scrapper.dao.link.repository.jpa;

import backend.academy.scrapper.dao.link.entity.Link;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    Link findByUrl(String url);

    Link findByUrlAndChats_Id(String url, Long chatId);

    List<Link> findByChats_IdAndChatLinkTags_Tag_NameIn(Long chatId, List<String> tagNames);

    List<Link> findByChats_Id(Long chatId);

    long countByUrlContainingIgnoreCase(String substring);

    @Modifying
    @Query(
            value =
                    """
        DELETE FROM tr_link
        WHERE NOT EXISTS (
            SELECT 1
            FROM tr_chat_link_filter
            WHERE tr_chat_link_filter.link_id = tr_link.id
        )
        AND NOT EXISTS (
            SELECT 1
            FROM tr_chat_link_tag
            WHERE tr_chat_link_tag.link_id = tr_link.id
        )
        AND NOT EXISTS (
            SELECT 1
            FROM tr_chat_link
            WHERE tr_chat_link.link_id = tr_link.id
        )
        """,
            nativeQuery = true)
    void removeOrphanedLinks();

    List<Link> findByLastTrackedAtLessThanOrLastTrackedAtIsNull(Long lastTrackedAt, Pageable pageable);
}
