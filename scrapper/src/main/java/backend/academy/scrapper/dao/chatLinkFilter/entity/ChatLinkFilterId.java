package backend.academy.scrapper.dao.chatLinkFilter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChatLinkFilterId(@Column Long chatId, @Column Long linkId, @Column Long filterId) {}
