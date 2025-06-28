package backend.academy.scrapper.dao.tag.service;

import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.dao.tag.repository.JpaTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaTagDaoService implements TagDaoService {

    private final JpaTagRepository jpaTagRepository;

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return jpaTagRepository.findByName(name);
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        return jpaTagRepository.save(tag);
    }

    @Override
    @Transactional
    public void removeOrphanedTags() {
        jpaTagRepository.removeOrphanedTags();
    }
}
