package backend.academy.scrapper.linkTracker;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkTrackerProvider {

    private final List<LinkTracker> linkTrackers;

    public LinkTracker getLinkTracker(URI url) {
        return linkTrackers.stream()
                .filter(linkTracker -> linkTracker.isSupports(url))
                .findFirst()
                .orElse(null);
    }

    public List<String> getAvailableUrls() {
        return linkTrackers.stream().map(LinkTracker::getLinkType).toList();
    }
}
