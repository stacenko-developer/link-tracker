package backend.academy.scrapper.metric;

import static backend.academy.scrapper.constants.MetricConstValues.GITHUB_LINKS_GAUGE_METRIC;
import static backend.academy.scrapper.constants.MetricConstValues.STACKOVERFLOW_LINKS_GAUGE_METRIC;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.dao.link.service.LinkDaoService;
import backend.academy.scrapper.scheduler.LinkTrackerScheduler;
import io.micrometer.core.instrument.Gauge;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class LinksGaugeTest extends CommonMetricTest {

    @MockitoBean
    private LinkDaoService linkDaoService;

    @MockitoBean
    private LinkTrackerScheduler linkTrackerScheduler;

    @Test
    public void changeGithubLinksCount_GithubLinksGaugeShouldBeChanged() {
        changeLinksCountForTestUpdatingGauge(GITHUB_LINKS_GAUGE_METRIC, () -> linkDaoService.getGithubLinksCount());
    }

    @Test
    public void changeStackoverflowLinksCount_StackoverflowLinksGaugeShouldBeChanged() {
        changeLinksCountForTestUpdatingGauge(
                STACKOVERFLOW_LINKS_GAUGE_METRIC, () -> linkDaoService.getStackoverflowLinksCount());
    }

    @Test
    public void testGithubLinksGaugeContainingInMeterRegistry() {
        testGaugeContainingInMeterRegistry(GITHUB_LINKS_GAUGE_METRIC);
    }

    @Test
    public void testStackoverflowLinksGaugeContainingInMeterRegistry() {
        testGaugeContainingInMeterRegistry(STACKOVERFLOW_LINKS_GAUGE_METRIC);
    }

    private void changeLinksCountForTestUpdatingGauge(
            String linksGaugeMetricName, Supplier<Long> getLinksCountSupplier) {
        long firstValue = 1;
        long secondValue = 2;

        when(getLinksCountSupplier.get()).thenReturn(firstValue).thenReturn(secondValue);
        Gauge linksGauge = meterRegistry.find(linksGaugeMetricName).gauge();

        Assertions.assertNotNull(linksGauge);
        Assertions.assertEquals(firstValue, linksGauge.value());

        linksGauge = meterRegistry.find(linksGaugeMetricName).gauge();

        Assertions.assertNotNull(linksGauge);
        Assertions.assertEquals(secondValue, linksGauge.value());
    }
}
