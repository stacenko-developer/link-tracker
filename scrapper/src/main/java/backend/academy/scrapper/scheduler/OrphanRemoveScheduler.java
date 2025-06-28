package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.dao.filter.service.FilterDaoService;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.dao.tag.service.TagDaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrphanRemoveScheduler {

    private final LinkDaoService linkDaoService;
    private final FilterDaoService filterDaoService;
    private final TagDaoService tagDaoService;

    @Scheduled(fixedRateString = "${scheduler.orphan-remove.period-minutes}")
    public void orphanRemove() {
        linkDaoService.removeOrphanedLinks();
        filterDaoService.removeOrphanedFilters();
        tagDaoService.removeOrphanedTags();
    }
}
