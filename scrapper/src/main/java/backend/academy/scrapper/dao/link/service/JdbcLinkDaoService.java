package backend.academy.scrapper.dao.link.service;

import backend.academy.scrapper.dao.link.entity.Link;
import backend.academy.scrapper.dao.link.repository.jdbc.JdbcLinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class JdbcLinkDaoService implements LinkDaoService {

    private final JdbcLinkRepository jdbcLinkRepository;

    @Override
    @Transactional(readOnly = true)
    public Link findByUrlAndChatId(URI url, Long chatId) {
        return jdbcLinkRepository.findByUrlAndChatId(url.toString(), chatId);
    }

    @Override
    @Transactional(readOnly = true)
    public Link findByUrl(URI url) {
        return jdbcLinkRepository.findByUrl(url.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getAllOldLinks(Long lastTrackedAtBefore, Integer limit) {
        return jdbcLinkRepository.findAll(lastTrackedAtBefore, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Link> getAllUserTrackingLinks(Long chatId, List<String> tagNames) {
        return jdbcLinkRepository.getAllUserTrackingLinks(chatId, tagNames);
    }

    @Override
    @Transactional
    public Link save(Link link) {
        return jdbcLinkRepository.save(link);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getGithubLinksCount() {
        return jdbcLinkRepository.getGithubLinksCount();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public long getStackoverflowLinksCount() {
        return jdbcLinkRepository.getStackoverflowLinksCount();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAfterTracking(URI url, Long lastUpdatedAt, Long lastTrackedAt) {
        Link link = jdbcLinkRepository.findByUrl(url.toString());

        if (link == null) {
            return;
        }

        jdbcLinkRepository.updateAfterTracking(url.toString(), lastUpdatedAt, lastTrackedAt);
    }

    @Override
    @Transactional
    public void removeOrphanedLinks() {
        jdbcLinkRepository.removeOrphanedLinks();
    }
}
