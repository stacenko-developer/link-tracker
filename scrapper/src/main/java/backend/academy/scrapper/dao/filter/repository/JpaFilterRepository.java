package backend.academy.scrapper.dao.filter.repository;

import backend.academy.scrapper.dao.filter.entity.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaFilterRepository extends JpaRepository<Filter, Long> {

    Filter findByKeyAndValue(String key, String value);

    @Modifying
    @Query(
            value = "DELETE FROM tr_filter WHERE NOT EXISTS (SELECT 1 FROM tr_chat_link_filter "
                    + "WHERE tr_chat_link_filter.filter_id = tr_filter.id LIMIT 1)",
            nativeQuery = true)
    void removeOrphanedFilters();
}
