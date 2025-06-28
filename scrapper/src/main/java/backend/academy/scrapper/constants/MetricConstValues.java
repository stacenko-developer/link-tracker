package backend.academy.scrapper.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetricConstValues {

    public static final String GITHUB_LINKS_GAUGE_METRIC = "github_links_count";
    public static final String STACKOVERFLOW_LINKS_GAUGE_METRIC = "stackoverflow_links_count";

    public static final String GITHUB_SCRAPE_TIMER_METRIC = "github_scrape_time";
    public static final String STACKOVERFLOW_SCRAPE_TIMER_METRIC = "stackoverflow_scrape_time";
}
