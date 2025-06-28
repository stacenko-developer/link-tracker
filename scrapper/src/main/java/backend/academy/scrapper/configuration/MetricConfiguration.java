package backend.academy.scrapper.configuration;

import static backend.academy.scrapper.constants.MetricConstValues.GITHUB_LINKS_GAUGE_METRIC;
import static backend.academy.scrapper.constants.MetricConstValues.GITHUB_SCRAPE_TIMER_METRIC;
import static backend.academy.scrapper.constants.MetricConstValues.STACKOVERFLOW_LINKS_GAUGE_METRIC;
import static backend.academy.scrapper.constants.MetricConstValues.STACKOVERFLOW_SCRAPE_TIMER_METRIC;

import backend.academy.scrapper.dao.link.service.LinkDaoService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.function.ToDoubleFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    private static final double[] PERCENTILES = {0.5, 0.95, 0.99};

    @Bean
    public Gauge githubLinksGauge(MeterRegistry meterRegistry, LinkDaoService linkDaoService) {
        return buildLinkGauge(
                GITHUB_LINKS_GAUGE_METRIC, linkDaoService, LinkDaoService::getGithubLinksCount, meterRegistry);
    }

    @Bean
    public Gauge stackoverflowLinksGauge(MeterRegistry meterRegistry, LinkDaoService linkDaoService) {
        return buildLinkGauge(
                STACKOVERFLOW_LINKS_GAUGE_METRIC,
                linkDaoService,
                LinkDaoService::getStackoverflowLinksCount,
                meterRegistry);
    }

    @Bean
    public Timer githubScrapeTimer(MeterRegistry meterRegistry) {
        return buildScrapeTimer(GITHUB_SCRAPE_TIMER_METRIC, meterRegistry);
    }

    @Bean
    public Timer stackoverflowScrapeTimer(MeterRegistry meterRegistry) {
        return buildScrapeTimer(STACKOVERFLOW_SCRAPE_TIMER_METRIC, meterRegistry);
    }

    private Gauge buildLinkGauge(
            String name, LinkDaoService linkDaoService, ToDoubleFunction<LinkDaoService> func, MeterRegistry registry) {
        return Gauge.builder(name, linkDaoService, func).register(registry);
    }

    private Timer buildScrapeTimer(String name, MeterRegistry registry) {
        return Timer.builder(name).publishPercentiles(PERCENTILES).register(registry);
    }
}
