package backend.academy.scrapper.dao.tag.service;

import backend.academy.scrapper.dao.tag.entity.Tag;

public interface TagDaoService {

    Tag findByName(String name);

    Tag createTag(Tag tag);

    void removeOrphanedTags();
}
