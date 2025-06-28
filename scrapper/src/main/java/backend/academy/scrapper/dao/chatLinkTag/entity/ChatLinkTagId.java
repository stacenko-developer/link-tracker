package backend.academy.scrapper.dao.chatLinkTag.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChatLinkTagId(@Column Long chatId, @Column Long linkId, @Column Long tagId) {}
