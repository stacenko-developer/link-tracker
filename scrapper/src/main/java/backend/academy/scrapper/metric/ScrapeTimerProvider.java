package backend.academy.scrapper.metric;

import static backend.academy.scrapper.enums.LinkType.GITHUB;
import static backend.academy.scrapper.enums.LinkType.STACKOVERFLOW;
import static java.util.Map.entry;

import backend.academy.scrapper.enums.LinkType;
import io.micrometer.core.instrument.Timer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapeTimerProvider {

    private final Timer githubScrapeTimer;
    private final Timer stackoverflowScrapeTimer;

    public Timer getScrapeTimer(String url) {
        LinkType linkType = LinkType.getLinkType(url);

        return getScrapeTimers().get(linkType);
    }

    private Map<LinkType, Timer> getScrapeTimers() {
        return Map.ofEntries(entry(GITHUB, githubScrapeTimer), entry(STACKOVERFLOW, stackoverflowScrapeTimer));
    }
}
