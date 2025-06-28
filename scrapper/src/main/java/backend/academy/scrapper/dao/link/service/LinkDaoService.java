package backend.academy.scrapper.dao.link.service;

import backend.academy.scrapper.dao.link.entity.Link;
import java.net.URI;
import java.util.List;

public interface LinkDaoService {

    Link findByUrlAndChatId(URI url, Long chatId);

    Link findByUrl(URI url);

    List<Link> getAllOldLinks(Long lastTrackedAtBefore, Integer limit);

    List<Link> getAllUserTrackingLinks(Long tgChatId, List<String> tagNames);

    Link save(Link link);

    long getGithubLinksCount();

    long getStackoverflowLinksCount();

    void updateAfterTracking(URI url, Long lastUpdatedAt, Long lastTrackedAt);

    void removeOrphanedLinks();
}
