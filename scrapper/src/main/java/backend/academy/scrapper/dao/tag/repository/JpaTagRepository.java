package backend.academy.scrapper.dao.tag.repository;

import backend.academy.scrapper.dao.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);

    @Modifying
    @Query(
            value = "DELETE FROM tr_tag WHERE NOT EXISTS (SELECT 1 FROM tr_chat_link_tag "
                    + "WHERE tr_chat_link_tag.tag_id = tr_tag.id LIMIT 1)",
            nativeQuery = true)
    void removeOrphanedTags();
}
