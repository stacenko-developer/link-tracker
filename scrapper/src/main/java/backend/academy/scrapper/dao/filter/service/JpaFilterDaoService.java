package backend.academy.scrapper.dao.filter.service;

import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.filter.repository.JpaFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaFilterDaoService implements FilterDaoService {

    private final JpaFilterRepository jpaFilterRepository;

    @Override
    @Transactional(readOnly = true)
    public Filter findByKeyAndValue(String key, String value) {
        return jpaFilterRepository.findByKeyAndValue(key, value);
    }

    @Override
    @Transactional
    public Filter createFilter(Filter filter) {
        return jpaFilterRepository.save(filter);
    }

    @Override
    @Transactional
    public void removeOrphanedFilters() {
        jpaFilterRepository.removeOrphanedFilters();
    }
}
