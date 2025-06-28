package backend.academy.scrapper.dao.tag.service;

import backend.academy.scrapper.dao.tag.entity.Tag;
import backend.academy.scrapper.dao.tag.repository.JdbcTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcTagDaoService implements TagDaoService {

    private final JdbcTagRepository jdbcTagRepository;

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return jdbcTagRepository.findByName(name);
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        return jdbcTagRepository.create(tag);
    }

    @Override
    @Transactional
    public void removeOrphanedTags() {
        jdbcTagRepository.removeOrphanedTags();
    }
}
