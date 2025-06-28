package backend.academy.scrapper.dao.filter.service;

import backend.academy.scrapper.dao.filter.entity.Filter;

public interface FilterDaoService {

    Filter findByKeyAndValue(String key, String value);

    Filter createFilter(Filter filter);

    void removeOrphanedFilters();
}
