package backend.academy.scrapper.metric;

import static backend.academy.scrapper.constants.MetricConstValues.GITHUB_SCRAPE_TIMER_METRIC;
import static backend.academy.scrapper.constants.MetricConstValues.STACKOVERFLOW_SCRAPE_TIMER_METRIC;
import static org.mockito.Mockito.when;

import backend.academy.common.dto.ResponseDto;
import backend.academy.scrapper.dao.chat.entity.Chat;
import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.linkTracker.LinkHandler;
import backend.academy.scrapper.linkTracker.LinkTrackerProvider;
import backend.academy.scrapper.service.client.GithubClientService;
import backend.academy.scrapper.service.client.StackoverflowClientService;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class ScrapeTimerTest extends CommonMetricTest {

    @Autowired
    private LinkHandler linkHandler;

    @Autowired
    private LinkTrackerProvider linkTrackerProvider;

    @MockitoBean
    private LinkDaoService linkDaoService;

    @MockitoBean
    private GithubClientService githubClientService;

    @MockitoBean
    private StackoverflowClientService stackoverflowClientService;

    @Test
    public void scrapeGithubLink_ShouldUpdateGithubScrapeTimer() {
        String owner = "owner";
        String repo = "repo";
        String githubUrl = String.format("https://github.com/%s/%s", owner, repo);

        when(githubClientService.getRepositoryEvents(owner, repo)).thenReturn(new ResponseDto<>(null, null));

        scrapeStackoverflowLink_ShouldUpdateStackoverflowScrapeTimer(githubUrl, GITHUB_SCRAPE_TIMER_METRIC);
    }

    @Test
    public void scrapeStackoverflowLink_ShouldUpdateStackoverflowScrapeTimer() {
        Long questionId = 1L;
        String stackoverflowUrl = String.format("https://stackoverflow.com/questions/%s", questionId);

        when(stackoverflowClientService.getQuestionInformation(questionId)).thenReturn(new ResponseDto<>(null, null));

        scrapeStackoverflowLink_ShouldUpdateStackoverflowScrapeTimer(
                stackoverflowUrl, STACKOVERFLOW_SCRAPE_TIMER_METRIC);
    }

    private void scrapeStackoverflowLink_ShouldUpdateStackoverflowScrapeTimer(
            String url, String scrapeTimerMetricName) {
        int correctMeasurementsCount = 1;

        Link link = new Link();
        link.url(url);
        link.chats(List.of(new Chat()));

        linkHandler.handleLinks(List.of(link));

        Timer scrapeTimer = meterRegistry.find(scrapeTimerMetricName).timer();

        Assertions.assertNotNull(scrapeTimer);
        Assertions.assertEquals(correctMeasurementsCount, scrapeTimer.count());
        Assertions.assertTrue(scrapeTimer.totalTime(scrapeTimer.baseTimeUnit()) > 0);
    }

    @Test
    public void testGithubScrapeTimerContainingInMeterRegistry() {
        testTimerContainingInMeterRegistry(GITHUB_SCRAPE_TIMER_METRIC);
    }

    @Test
    public void testStackoverflowScrapeTimerContainingInMeterRegistry() {
        testTimerContainingInMeterRegistry(STACKOVERFLOW_SCRAPE_TIMER_METRIC);
    }
}
