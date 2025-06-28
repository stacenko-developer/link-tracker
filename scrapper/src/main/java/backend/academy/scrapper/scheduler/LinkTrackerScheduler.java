package backend.academy.scrapper.scheduler;

import backend.academy.common.utils.DateTimeUtils;
import backend.academy.scrapper.configuration.scheduler.LinkTrackingSchedulerProperties;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.linkTracker.LinkHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkTrackerScheduler {

    private static final String START_TRACKING_LOG_MESSAGE = "Начало отслеживания обновления ссылок";
    private static final String END_TRACKING_LOG_MESSAGE = "Завершение отслеживания обновления ссылок";

    private static final int THREADS_COUNT_FOR_SINGLE_THREAD_TRACKING = 1;

    private final LinkTrackingSchedulerProperties linkTrackingSchedulerProperties;

    private final LinkDaoService linkDaoService;
    private final ExecutorService executorService;
    private final LinkHandler linkHandler;

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.track-updating-links.period-minutes}")
    public void trackUpdatingLinks() {
        logStartTrackingProcess();

        List<Link> links = linkDaoService.getAllOldLinks(
                DateTimeUtils.getNowUtc()
                        - linkTrackingSchedulerProperties.retryDelayMinutes().toMillis(),
                linkTrackingSchedulerProperties.linksLimitPerTrack());

        if (linkTrackingSchedulerProperties.threadsCount() == THREADS_COUNT_FOR_SINGLE_THREAD_TRACKING) {
            linkHandler.handleLinks(links);
            logEndTrackingProcess();
            return;
        }

        multiThreadedProcessLinks(links);
    }

    private void multiThreadedProcessLinks(List<Link> links) {
        List<List<Link>> batches = getBatchesLinks(links, linkTrackingSchedulerProperties.threadsCount());

        batches.stream()
                .filter(batch -> !batch.isEmpty())
                .map(batch -> CompletableFuture.runAsync(() -> linkHandler.handleLinks(batch), executorService))
                .toList()
                .forEach(CompletableFuture::join);

        logEndTrackingProcess();
    }

    private List<List<Link>> getBatchesLinks(List<Link> links, int parts) {
        int batchSize = links.size() / parts;
        List<List<Link>> batches = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            int fromIndex = i * batchSize;
            int toIndex = (i == parts - 1) ? links.size() : (i + 1) * batchSize;
            batches.add(links.subList(fromIndex, toIndex));
        }

        return batches;
    }

    private void logStartTrackingProcess() {
        log.info(START_TRACKING_LOG_MESSAGE);
    }

    private void logEndTrackingProcess() {
        log.info(END_TRACKING_LOG_MESSAGE);
    }
}
