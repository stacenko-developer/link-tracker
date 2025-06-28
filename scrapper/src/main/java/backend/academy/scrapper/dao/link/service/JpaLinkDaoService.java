package backend.academy.scrapper.dao.link.service;

import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.repository.jpa.JpaLinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaLinkDaoService implements LinkDaoService {

    private static final String GITHUB_URL_TYPE_SUBSTRING = "github";
    private static final String STACKOVERFLOW_URL_TYPE_SUBSTRING = "stackoverflow";

    private static final int DEFAULT_PAGE_NUMBER = 0;

    private final JpaLinkRepository jpaLinkRepository;

    @Override
    @Transactional(readOnly = true)
    public Link findByUrlAndChatId(URI url, Long chatId) {
        return jpaLinkRepository.findByUrlAndChats_Id(url.toString(), chatId);
    }

    @Override
    @Transactional(readOnly = true)
    public Link findByUrl(URI url) {
        return jpaLinkRepository.findByUrl(url.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getAllOldLinks(Long lastTrackedAtBefore, Integer limit) {
        Pageable pageable = PageRequest.of(DEFAULT_PAGE_NUMBER, limit);
        return jpaLinkRepository.findByLastTrackedAtLessThanOrLastTrackedAtIsNull(lastTrackedAtBefore, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getAllUserTrackingLinks(Long chatId, List<String> tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            return jpaLinkRepository.findByChats_IdAndChatLinkTags_Tag_NameIn(chatId, tagNames);
        }

        return jpaLinkRepository.findByChats_Id(chatId);
    }

    @Override
    @Transactional
    public Link save(Link link) {
        return jpaLinkRepository.save(link);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getGithubLinksCount() {
        return jpaLinkRepository.countByUrlContainingIgnoreCase(GITHUB_URL_TYPE_SUBSTRING);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getStackoverflowLinksCount() {
        return jpaLinkRepository.countByUrlContainingIgnoreCase(STACKOVERFLOW_URL_TYPE_SUBSTRING);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAfterTracking(URI url, Long lastUpdatedAt, Long lastTrackedAt) {
        Link link = jpaLinkRepository.findByUrl(url.toString());

        if (link == null) {
            return;
        }

        if (lastUpdatedAt != null) {
            link.lastUpdatedAt(lastUpdatedAt);
        }

        if (lastTrackedAt != null) {
            link.lastTrackedAt(lastTrackedAt);
        }

        jpaLinkRepository.save(link);
    }

    @Override
    @Transactional
    public void removeOrphanedLinks() {
        jpaLinkRepository.removeOrphanedLinks();
    }
}
