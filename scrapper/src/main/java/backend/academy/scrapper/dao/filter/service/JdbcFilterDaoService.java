package backend.academy.scrapper.dao.filter.service;

import backend.academy.scrapper.dao.filter.entity.Filter;
import backend.academy.scrapper.dao.filter.repository.JdbcFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcFilterDaoService implements FilterDaoService {

    private final JdbcFilterRepository jdbcFilterRepository;

    @Override
    @Transactional(readOnly = true)
    public Filter findByKeyAndValue(String key, String value) {
        return jdbcFilterRepository.findByKeyAndValue(key, value);
    }

    @Override
    @Transactional
    public Filter createFilter(Filter filter) {
        return jdbcFilterRepository.create(filter);
    }

    @Override
    @Transactional
    public void removeOrphanedFilters() {
        jdbcFilterRepository.removeOrphanedFilters();
    }
}
